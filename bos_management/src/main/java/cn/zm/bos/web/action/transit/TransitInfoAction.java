package cn.zm.bos.web.action.transit;

import java.util.HashMap;
import java.util.Map;

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

import cn.zm.bos.domain.transit.TransitInfo;
import cn.zm.bos.service.transit.TransitInfoService;
import cn.zm.bos.web.action.common.BaseAction;

import com.opensymphony.xwork2.ActionContext;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TransitInfoAction extends BaseAction<TransitInfo> {

	private String wayBillIds;

	public void setWayBillIds(String wayBillIds) {
		this.wayBillIds = wayBillIds;
	}
	
	@Autowired
	private TransitInfoService transitInfoService;
	
	@Action(value="transit_save",results={@Result(name="success",type="json")})
	public String save(){
		//调用业务层，保存transitInfo
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			transitInfoService.save(wayBillIds);
			//成功
			result.put("success", true);
			result.put("msg", "开启中转配送成功");
		} catch (Exception e) {
			e.printStackTrace();
			//失败
			result.put("success", false);
			result.put("msg", "开启中转配送失败，异常："+e.getMessage());
		}
		
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	
	@Action(value="transitInfo_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		///分页查询
		Pageable pageable = new PageRequest(page-1, rows);
		//调用业务层进行分页查询
		
		Page<TransitInfo> pageData = transitInfoService.findAll(pageable);
		
		//压入值栈
		pushPageDateToValueStack(pageData);
		return SUCCESS;
		
	}

}
