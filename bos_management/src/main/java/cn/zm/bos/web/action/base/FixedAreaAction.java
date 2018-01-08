package cn.zm.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.domain.base.FixedArea;
import cn.zm.bos.service.base.FixedAreaService;
import cn.zm.bos.web.action.common.BaseAction;
import cn.zm.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {

	@Autowired
	private FixedAreaService fixedAreaService;

	// 添加或修改
	@Action(value = "fixedArea_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String save() {
		fixedAreaService.save(model);
		return SUCCESS;
	}

	// 有无条件分页查询
	@Action(value = "fixedArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		Pageable pageable = new PageRequest(page - 1, rows);
		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root<FixedArea> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class),
							model.getId());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(
							root.get("company").as(String.class),
							"%" + model.getCompany() + "%");
					list.add(p2);
				}
				// 多表关联
				// 分区表
//				if (model.getSubareas() != null) {
//					Join<Object, Object> subArearoot = root.join("subareas",
//							JoinType.INNER);
//					Predicate p3 = cb.equal(
//							subArearoot.get("id").as(Set.class),
//							model.getSubareas());
//					list.add(p3);
//				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		Page<FixedArea> page = fixedAreaService.pageQuery(specification,pageable);
		pushPageDateToValueStack(page);

		return SUCCESS;
	}

	private String ids;

	public void setIds(String ids) {
		this.ids = ids;
	}

	// 批量删除
	@Action(value = "fixedArea_delBatch", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String delBatch() {
		String[] fixedAreaIds = ids.split(",");
		fixedAreaService.delBatch(fixedAreaIds);
		return SUCCESS;
	}

	// 查询未关联的客户
	@Action(value = "fixedArea_noAssociationCustomer", results = { @Result(name = "success", type = "json") })
	public String findNoAssociationCustomer() {
		// 使用WebClient调用webService接口
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9002/crm_management/services/customerService/noassociationcustomer")
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).getCollection(Customer.class);

		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 查询已经关联的客户
	@Action(value = "fixedArea_hasAssociationFixedAreaCustomer", results = { @Result(name = "success", type = "json") })
	public String findHasAssociationFixedAreaCustomer() {
		// 使用WebClient调用webService接口
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9002/crm_management/services/customerService/hasAssociationfixedareacustomer/"
						+ model.getId()).accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).getCollection(Customer.class);

		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 属性驱动
	private String[] customerIds;

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	// 将客户关联到定区
	@Action(value = "fixedArea_associationCustomerToFixedArea", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCustomerToFixedArea() {

		String customerIdStr = StringUtils.join(customerIds, ",");

		WebClient
				.create("http://localhost:9002/crm_management/services/customerService/associationcustomertofixedarea?fixedAreaId="
						+ model.getId() + "&customerIdStr=" + customerIdStr)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).put(null);

		return SUCCESS;
	}

	private Integer courierId; // 快递员ID
	private Integer takeTimeId; // 收派时间ID

	public void setCourierId(Integer courierId) {
		this.courierId = courierId;
	}

	public void setTakeTimeId(Integer takeTimeId) {
		this.takeTimeId = takeTimeId;
	}

	// 将快递员关联到定区
	@Action(value = "fixedArea_associationCourierToFixedArea", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCourierToFixedArea() {

		fixedAreaService.associationCourierToFixedArea(model, courierId,
				takeTimeId);
		return SUCCESS;
	}

	// 查询所有的定区
	@Action(value = "findAllFixedArea", results = { @Result(name = "success", type = "json") })
	public String findAllFixedArea() {
		List<FixedArea> areas = fixedAreaService.findAllFixedArea();
		ActionContext.getContext().getValueStack().push(areas);
		return SUCCESS;
	}

}
