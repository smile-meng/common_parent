package cn.zm.crm.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cn.zm.crm.domain.Customer;


public interface CustomerService {
	
	//查询所有未关联用户列表
	@Path("/noassociationcustomer")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Customer> findNoAssociationCustomer();
	
	//查询所有关联的用户列表
	@Path("/hasAssociationfixedareacustomer/{fixedareaid}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Customer> findHasAssociationFixedAreaCustomer(@PathParam("fixedareaid")String fixedAreaId);
	
	//关联没有指定定区的客户
	@Path("/associationcustomertofixedarea")
	@PUT
	public void associationCustomerToFixedArea(@QueryParam("customerIdStr") String customerIdStr,
			@QueryParam("fixedAreaId")String fixedAreaId);
	
	//保存注册的用户信息
	@Path("/customer")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void regist(Customer customer);
	
	//用户的手机号码查用户
	@Path("/customer/telephone/{telephone}")
	@GET
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Customer findByTelephone(@PathParam("telephone")String telephone);
	
	//利用手机号码修改用户的绑定类型
	@Path("/customer/updateType/{telephone}")
	@PUT
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void updateType(@PathParam("telephone")String telephone);
	
	
	//前台登录进行判断
	@Path("/customer/login")
	@GET
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Customer findByTelephoneAndPassword(@QueryParam("telephone")String telephone,
			@QueryParam("password")String password);
	
	//根据地址获得客户的定区编码
	@Path("/customer/findFixedAreaIdByAddress")
	@GET
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public String findFindAreaIdByAddress(@QueryParam("address")String address);
	
	
}
