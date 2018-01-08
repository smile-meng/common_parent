package cn.zm.bos.web.action.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
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

import cn.zm.bos.domain.base.Courier;
import cn.zm.bos.service.base.CourierService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CourierAction extends ActionSupport implements
		ModelDriven<Courier> {

	private Courier courier = new Courier();

	@Override
	public Courier getModel() {
		return courier;
	}

	@Autowired
	private CourierService courierService;

	private Integer page;// 第几页
	private Integer rows;// 每页显示的条数

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	private String ids;// 批量作废的id

	public void setIds(String ids) {
		this.ids = ids;
	}

	@Action(value = "courier_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String save() {
		courierService.save(courier);
		return SUCCESS;
	}

	@Action(value = "courier_queryQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		// 根据条件查询
		Specification<Courier> specification = new Specification<Courier>() {
			/*
			 * 构造条件查询方法，返回null代表无条件查询 root 参数 获取条件表达式 name=? CriteriaQuery
			 * 参数，构造简单查询条件返回 提供where方法 CriteriaBuilder 参数
			 * 构造Predicate对象，条件对象，构造复杂效果
			 */
			@Override
			public Predicate toPredicate(Root<Courier> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				// 将查询的Predicate添加到集合中
				List<Predicate> list = new ArrayList<Predicate>();
				// 单表查询
				// 快递员员工查询
				if (StringUtils.isNotBlank(courier.getCourierNum())) {
					Predicate p1 = cb.equal(
							root.get("courierNum").as(String.class),
							courier.getCourierNum());
					list.add(p1);
				}
				// 所属单位模糊查询
				if (StringUtils.isNotBlank(courier.getCompany())) {
					Predicate p2 = cb.like(root.get("type").as(String.class),
							"%" + courier.getCompany() + "%");
					list.add(p2);
				}
				// 类型查询
				if (StringUtils.isNotBlank(courier.getType())) {
					Predicate p3 = cb.equal(root.get("type").as(String.class),
							courier.getType());
					list.add(p3);
				}
				// 多表查询
				// 使用Courier(Root),关联standard 进行收派标准的名称进行模糊查询
				Join<Object, Object> standardRoot = root.join("standard",
						JoinType.INNER);// 设置内连接进行关联
				if (courier.getStandard() != null
						&& StringUtils.isNotBlank(courier.getStandard()
								.getName())) {
					Predicate p4 = cb.like(
							standardRoot.get("name").as(String.class), "%"
									+ courier.getStandard().getName() + "%");
					list.add(p4);
				}
				// 将集合转换为数组

				return cb.and(list.toArray(new Predicate[0]));// 空数组代表泛型
			}
		};
		// 分页查询
		Pageable pageable = new PageRequest(page - 1, rows);
		Page<Courier> page = courierService.findAll(specification, pageable);

		Map<String, Object> results = new HashMap<String, Object>();
		results.put("total", page.getTotalElements());
		results.put("rows", page.getContent());
		
		// 将map集合转换为json压到栈顶
		ServletActionContext.getContext().getValueStack().push(results);
		return SUCCESS;
	}

	@Action(value = "courier_delBatch", results = { @Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String delBatch() {
		//将前台返回的ids进行分割
		String[] idStrs = ids.split(",");
		//调用业务层进行作废
		courierService.delBatch(idStrs);
		return SUCCESS;
	}
	
	@Action(value = "courier_addBatch", results = { @Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String addBatch() {
		//将前台返回的ids进行分割
		String[] idStrs = ids.split(",");
		//调用业务层进行还原
		courierService.addBatch(idStrs);
		return SUCCESS;
	}
	
	//查询未关联的快递员
	@Action(value="courier_findNoAssociation",results={@Result(name="success",type="json")})
	public String findNoAssociation(){
		
		List<Courier> couriers = courierService.findNoAssociation();
		
		ActionContext.getContext().getValueStack().push(couriers);
		return SUCCESS;
	}
	
	
	
}
