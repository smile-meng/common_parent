package cn.zm.bos.web.action.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.base.Standard;
import cn.zm.bos.service.base.StandardService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


@ParentPackage("json-default")
@Namespace("/")
@Actions
@Controller
@Scope("prototype")
public class StandardAction extends ActionSupport implements ModelDriven<Standard>{

	private Standard standard = new Standard();
	
	@Autowired
	private StandardService standardService;
	
	@Override
	public Standard getModel() {
		return standard;
	}
	
	//属性驱动
	private int page;
	private int rows;
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Action(value="standard_save",results={@Result(name="success",location="./pages/base/standard.html",type="redirect")})
	public String save(){
		standardService.save(standard);
		return SUCCESS;
	}
	
	@Action(value="standard_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//调用业务层，查询数据结果
		Pageable pageable = new PageRequest(page-1, rows);
		Page<Standard> pageData = standardService.findPageData(pageable); 
		
		//返回客户端数据需要total 和rows
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());
		
		//将map转换为json数据返回，使用
//		Object json = JSONObject.toJSON(result);
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	
	//查询所有的standard为了将name显示到快递员的信息中
	@Action(value="standard_findAll",results={@Result(name="success",type="json")})
	public String findAll(){
		List<Standard> list = standardService.findAll();
		
		ActionContext.getContext().getValueStack().push(list);
		
		return SUCCESS;
	}
}
