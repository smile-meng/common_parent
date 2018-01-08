package cn.zm.bos.service.take_delivery;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import cn.zm.bos.domain.take_delivery.Order;

public interface OrderService {
	
	//保存订单的webService服务
	@Path("/order")
	@POST
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public void saveOrder(Order order);

	//根据订单号查询订单详情
	public Order findByOrderNum(String orderNum);
	
}
