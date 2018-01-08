package cn.zm.bos.web.action;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.constant.Constants;
import cn.zm.bos.utils.MailUtils;
import cn.zm.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {

	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;

	@Action(value = "customer_sendSms")
	public String sendSms() throws Exception {
		// 手机号保存在Customer对象中
		// 生成短信验证码
		String randomCode = RandomStringUtils.randomNumeric(4); // 4位验证码
		// 将短信验证码 保存到session
		ServletActionContext.getRequest().getSession()
				.setAttribute(model.getTelephone(), randomCode);

		System.out.println("生成手机验证码为：" + randomCode);

		// 编辑短信内容
		final String msg = "尊敬的用户您好，本次获取的验证码为：" + randomCode + ",服务电话：12345678";

		jmsTemplate.send("bos_sms", new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", model.getTelephone());
				mapMessage.setString("msg", msg);
				return mapMessage;
			}
		});
		return NONE;
	}

	private String checkCode; // 验证码

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Action(value = "customer_regist", results = {
			@Result(name = "success", type = "redirect", location = "signup-success.html"),
			@Result(name = "input", type = "redirect", location = "signup.html") })
	public String regist() {

		String checkCodeSession = (String) ServletActionContext.getRequest()
				.getSession().getAttribute(model.getTelephone());

		if (checkCodeSession == null || !checkCode.equals(checkCodeSession)) {
			System.out.println("验证码错误");
			return INPUT;
		}

		WebClient
				.create("http://localhost:9002/crm_management/services/customerService/customer")
				.type(MediaType.APPLICATION_JSON).post(model);
		System.out.println("注册成功");

		// 注册成功发送一封邮件
		// 生成激活码
		String activecode = RandomStringUtils.randomNumeric(32);

		// 将激活码保持到redis，设置24小时失效
		redisTemplate.opsForValue().set(model.getTelephone(), activecode, 24,
				TimeUnit.HOURS);

		// 调用MailUtils发送激活邮件

		String content = "<h3>尊敬的客户您好，请于24小时内，进行邮箱账号的绑定，点击下面地址完成绑定:<br/><a href="
				+ MailUtils.activeUrl
				+ "?telephone="
				+ model.getTelephone()
				+ "&activecode=" + activecode + ">邮箱绑定地址</a></h3>";

		MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());

		return SUCCESS;
	}

	private String activecode;

	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}

	@Action(value = "customer_activeMail")
	public String activeMail() throws IOException {

		ServletActionContext.getResponse().setContentType(
				"text/html;charset=UTF-8");

		// 判断激活码是否有效
		String activeCodeRedis = redisTemplate.opsForValue().get(
				model.getTelephone());

		if (activeCodeRedis == null || !activeCodeRedis.equals(activecode)) {

			ServletActionContext.getResponse().getWriter()
					.println("激活码无效，请重新绑定邮箱");
		} else {
			// 激活码有效
			// 防止重复调用
			// 调用crm_management系统查询客户，判断是否已经绑定邮箱
			Customer customer = WebClient
					.create("http://localhost:9002/crm_management/services/customerService/customer/telephone/"
							+ model.getTelephone())
					.type(MediaType.APPLICATION_JSON).get(Customer.class);
			if (customer.getType() == null || customer.getType() != 1) {
				// 没有绑定，从而进行绑定
				WebClient
						.create("http://localhost:9002/crm_management/services/customerService/customer/updateType/"
								+ model.getTelephone()).put(customer);
				ServletActionContext.getResponse().getWriter()
						.println("邮箱绑定成功，请去登陆");
			} else {
				ServletActionContext.getResponse().getWriter()
						.println("邮箱已经绑定，无须重复绑定邮箱");
			}

			// 删除激活码
			redisTemplate.delete(model.getTelephone());
		}

		return NONE;
	}

	//客户登陆
	@Action(value = "customer_login", results = {
			@Result(name = "success", type = "redirect", location = "index.html#/myhome"),
			@Result(name = "login", type = "redirect", location = "login.html") })
	public String login() {
		Customer customer = WebClient
				.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/customer/login?telephone="
						+ model.getTelephone()
						+ "&password="
						+ model.getPassword())
				.accept(MediaType.APPLICATION_JSON).get(Customer.class);
		if (customer == null) {
			return LOGIN;
		} else {
			ServletActionContext.getRequest().getSession()
					.setAttribute("customer", customer);
			return SUCCESS;
		}
	}

}
