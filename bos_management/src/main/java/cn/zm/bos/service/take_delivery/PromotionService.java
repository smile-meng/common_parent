package cn.zm.bos.service.take_delivery;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.zm.bos.domain.page.PageBean;
import cn.zm.bos.domain.take_delivery.Promotion;

public interface PromotionService {

	//保存活动任务数据
	void save(Promotion promotion);

	//分页查询
	Page<Promotion> findPageData(Pageable pageable);
	
	//根据page 和rows返回分页数据
	@Path("/pageQuery")
	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public PageBean<Promotion> pageQuery(@QueryParam("page")int page,@QueryParam("rows")int rows);
	
	//根据id查询
	@Path("/promotion/{id}")
	@GET
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Promotion findById(@PathParam("id")Integer id);
	
	void updateStatus(Date date);
	
}
