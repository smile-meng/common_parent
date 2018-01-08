package cn.zm.bos.web.action.take_delivery;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.take_delivery.Order;
import cn.zm.bos.service.take_delivery.OrderService;
import cn.zm.bos.web.action.common.BaseAction;

import com.opensymphony.xwork2.ActionContext;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class OrderAction extends BaseAction<Order>{
	
	@Autowired
	private OrderService orderService;
	
	//根据订单号查询订单详情
	@Action(value="order_findByOrderNum",results={@Result(name="success",type="json")})
	public String findByOrderNum(){
		Order order = orderService.findByOrderNum(model.getOrderNum());
		Map<String,Object> result = new HashMap<String,Object>();
		if(order == null){
			//订单号不存在
			result.put("success", false);
		}else{
			//订单存在
			result.put("success", true);
			result.put("orderData", order);
		}
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
}
