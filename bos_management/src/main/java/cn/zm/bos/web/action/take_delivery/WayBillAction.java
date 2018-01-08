package cn.zm.bos.web.action.take_delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.take_delivery.WayBill;
import cn.zm.bos.service.take_delivery.WayBillService;
import cn.zm.bos.web.action.common.BaseAction;

import com.opensymphony.xwork2.ActionContext;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class WayBillAction extends BaseAction<WayBill>{
	
	private static final Logger LOGGER = Logger.getLogger(WayBillAction.class);
	
	@Autowired
	private WayBillService wayBillService; 
	
	//保存运单
	@Action(value="waybill_save",results={@Result(name="success",type="json")})
	public String save(){
		
		//去除没有id的order对象
		if(model.getOrder()!=null&&(model.getOrder().getId()==null||model.getOrder().getId()==0)){
			model.setOrder(null);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			wayBillService.save(model);
			//保存成功
			result.put("success", true);
			result.put("msg", "保存运单成功！");
			LOGGER.info("保存运单成功！,运单号：" + model.getWayBillNum());
			
		} catch (Exception e) {
			e.printStackTrace();
			//保存失败
			result.put("success", false);
			result.put("msg", "保存运单失败！");
			LOGGER.info("保存运单失败！,运单号：" + model.getWayBillNum(),e);
		}
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	
	//分页
	@Action(value="waybill_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		
		List<Order> orders = new ArrayList<Order>();
//		Order order1 = new Order(Direction.DESC,"username");
		Order order2 = new Order(Direction.DESC,"id");
//		orders.add(order1);
		orders.add(order2);
		//分页id顺序
		Pageable pageable = new PageRequest(page-1, rows, new Sort(orders));
		
		//调用业务层进行查询
		Page<WayBill> pageData = wayBillService.findPageData(model,pageable);
		//压入值栈返回
		pushPageDateToValueStack(pageData);
		
		return SUCCESS;
	}
	
	//根据运单号查询运单详情
	@Action(value="waybill_findByWayBillNum",results={@Result(name="success",type="json")})
	public String findByWayBillNum(){
		//调用业务层 查询
		WayBill wayBill = wayBillService.findByWayBillNum(model.getWayBillNum());
		Map<String,Object> result = new HashMap<String,Object>();
		if(wayBill == null){
			//运单不存在
			result.put("success", false);
		}else{
			//运单存在
			result.put("success", true);
			result.put("wayBillData", wayBill);
		}
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	
}
