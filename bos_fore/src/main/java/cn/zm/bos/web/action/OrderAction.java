package cn.zm.bos.web.action;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.base.Area;
import cn.zm.bos.domain.constant.Constants;
import cn.zm.bos.domain.take_delivery.Order;
import cn.zm.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class OrderAction extends BaseAction<Order> {

	private String sendAreaInfo;// 寄件人省市区
	private String recAreaInfo;// 收件人省市区

	public void setSendAreaInfo(String sendAreaInfo) {
		this.sendAreaInfo = sendAreaInfo;
	}

	public void setRecAreaInfo(String recAreaInfo) {
		this.recAreaInfo = recAreaInfo;
	}

	@Action(value = "order_save", results = {
			@Result(name = "success", type = "redirect", location = "index.html"),
			@Result(name = "error", type = "redirect", location = "login.html") })
	public String save() {
		// 寄件人区域
//		Area sendArea = new Area();
		String[] sendAreas = sendAreaInfo.split("/");
//		sendArea.setProvince(sendAreas[0]);
//		sendArea.setCity(sendAreas[1]);
//		sendArea.setDistrict(sendAreas[2]);
		
		 Area sendArea = WebClient
		 .create(Constants.BOS_MANAGEMENT_URL
		 + "/services/areaService/area?province="
		 + sendAreas[0] + "&city=" + sendAreas[1] + "&district="
		 + sendAreas[2]).accept(MediaType.APPLICATION_JSON)
		 .get(Area.class);

		// 收件人区域
//		Area recArea = new Area();
		String[] recAreas = recAreaInfo.split("/");
//		recArea.setProvince(recAreas[0]);
//		recArea.setCity(recAreas[1]);
//		recArea.setDistrict(recAreas[2]);

		Area recArea = WebClient
				.create(Constants.BOS_MANAGEMENT_URL
						+ "/services/areaService/area?province=" + recAreas[0]
						+ "&city=" + recAreas[1] + "&district=" + recAreas[2])
				.accept(MediaType.APPLICATION_JSON).get(Area.class);

		// 关联区域
		model.setSendArea(sendArea);
		model.setRecArea(recArea);

		// 关联客户
		Customer customer = (Customer) ServletActionContext.getRequest()
				.getSession().getAttribute("customer");
		// 判断用户是否登陆
		if (customer == null) {
			return ERROR;
		}
		model.setCustomer_id(customer.getId());

		// 调用webService进行保存订单
		WebClient
				.create(Constants.BOS_MANAGEMENT_URL
						+ "/services/orderService/order")
				.type(MediaType.APPLICATION_JSON).post(model);

		return SUCCESS;
	}

}
