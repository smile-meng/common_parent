package cn.zm.bos.service.base;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.zm.bos.domain.base.Area;

public interface AreaService {

	// 保持或修改区域
	void save(Area area);

	// 有无条件分页查询
	Page<Area> pageQuery(Specification<Area> specification, Pageable pageable);

	// 批量删除
	void delBatch(String[] idStrs);

	// 一键导入
	void batchImport(List<Area> list);

	// 查询所有的省份
	List<String> findAllProvince();

	// 查询所有的区域
	List<Area> findAll();

	// 根据省份查询市
	List<String> findCityByProvince(String province);

	// 根据市查询区（县）
	List<Area> findDistrictByCity(String city);

	// 根据省市区查询区域
	@Path("/area")
	@GET
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Area findByProvinceAndCityAndDistrict(
			@QueryParam("province") String province,
			@QueryParam("city") String city,
			@QueryParam("district") String district);

}
