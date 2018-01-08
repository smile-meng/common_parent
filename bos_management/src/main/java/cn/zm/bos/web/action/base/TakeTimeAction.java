package cn.zm.bos.web.action.base;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.domain.base.TakeTime;
import cn.zm.bos.service.base.TakeTimeService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TakeTimeAction extends BaseAction<TakeTime> {

	@Autowired
	private TakeTimeService takeTimeService;

	// 保存或者修改
	@Action(value = "stakeTime_save", results = { @Result(name = "success", type = "redirect", location = "pages/base/take_time.html") })
	public String save() {
		takeTimeService.save(model);
		return SUCCESS;
	}

	// 分页查询
	@Action(value = "takeTime_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		System.out.println(page + "-----------------");
		Pageable pageable = new PageRequest(page - 1, rows);

		Page<TakeTime> page = takeTimeService.pageQuery(pageable);

		pushPageDateToValueStack(page);

		return SUCCESS;
	}

	private String ids;

	public void setIds(String ids) {
		this.ids = ids;
	}

	// 批量删除
	@Action(value = "takeTime_delBatch", results = { @Result(name = "success", type = "redirect", location = "pages/base/take_time.html") })
	public String delBatch() {

		String[] idStr = ids.split(",");
		takeTimeService.delBatch(idStr);

		return SUCCESS;
	}

	// 查询所有的收派时间信息
	@Action(value="taketime_findAll",results={@Result(name="success",type="json")})
	public String findAll(){
		List<TakeTime> takeTimes = takeTimeService.findAll();
		ActionContext.getContext().getValueStack().push(takeTimes);
		return SUCCESS;
	}
}
