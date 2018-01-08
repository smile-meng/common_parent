package cn.zm.bos.web.action.system;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.domain.system.User;
import cn.zm.bos.service.system.UserService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User> {

	
	@Autowired
	private UserService userService;
	
	
	@Action(value = "user_login", results = {
			@Result(name = "login", type = "redirect", location = "login.html"),
			@Result(name = "success", type = "redirect", location = "index.html") })
	public String login() {
		// 基于shiro实现登陆
		Subject subject = SecurityUtils.getSubject();

		// 用户名和密码信息
		AuthenticationToken token = new UsernamePasswordToken(
				model.getUsername(), model.getPassword());
		try {
			subject.login(token);
			// 登陆成功
			// 将用户信息保存到session中
			return SUCCESS;
		} catch (Exception e) {
			// 登陆失败
			e.printStackTrace();
			return LOGIN;
		}
	}
	
	
	//注销用户
	@Action(value="user_logout",results = {
			@Result(name = "success", type = "redirect", location = "login.html")})
	public String logout(){
		
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return SUCCESS;
	}
	
	//查询用户列表
	@Action(value="user_list",results={@Result(name="success",type="json")})
	public String list(){
		List<User> users = userService.findAll();
		ActionContext.getContext().getValueStack().push(users);
		return SUCCESS;
	}
	
	//属性驱动
	private String[] roleIds;

	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}
	
	//保存用户
	@Action(value="user_save",results={@Result(name="success",type="redirect",location="pages/system/userlist.html")})
	public String save(){
		userService.save(model,roleIds);
		return SUCCESS;
	}
	
	
	
	
	
}
