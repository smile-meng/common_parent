package cn.zm.bos.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.constant.Constants;
import cn.zm.bos.domain.page.PageBean;
import cn.zm.bos.domain.take_delivery.Promotion;

import com.opensymphony.xwork2.ActionContext;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
@SuppressWarnings("all")
public class PromotionAction extends BaseAction<Promotion>{
	
	@Action(value="promotion_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		
		//基于webService 获取bos_management的活动列表 数据信息
		PageBean<Promotion> pageBean = WebClient.create(Constants.BOS_MANAGEMENT_URL+"/services/promotionService/pageQuery?page="+page+"&rows="+rows)
		.accept(MediaType.APPLICATION_JSON).get(PageBean.class);
		
		ActionContext.getContext().getValueStack().push(pageBean);
		return SUCCESS;
	}
	
	@Action(value="promotion_showDetail")
	public String showDetail() throws Exception{
		
		//根据ID判断对应得HTML是否存在，如果存在直接返回
		String htmlRealPath = ServletActionContext.getServletContext().getRealPath("/freemarker");
		
		File htmlFile = new File(htmlRealPath,model.getId()+".html");
		
		//如果html文件不存在，查询数据库，结合freemarker模板生成 页面
		if(!htmlFile.exists()){
			//配置对象
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
			//配置模板路径
			configuration.setDirectoryForTemplateLoading(new File(ServletActionContext.getServletContext().getRealPath("/WEB-INF/freemarker_template")));
			
			//获取模板对象
			Template template = configuration.getTemplate("promotion_detail.ftl");
			
			//动态数据
			Promotion promotion = WebClient.create(Constants.BOS_MANAGEMENT_URL+"/bos_management/services/promotionService/promotion/"+model.getId()).accept(MediaType.APPLICATION_JSON).get(Promotion.class);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("promotion", promotion);
			
			//合并输出
			template.process(map, new OutputStreamWriter(new FileOutputStream(htmlFile),"utf-8"));
			
		}
		//存在，直接将文件返回
		ServletActionContext.getResponse().setContentType("text/html,charset=utf-8");
		FileUtils.copyFile(htmlFile, ServletActionContext.getResponse().getOutputStream());
		
		return NONE;
	}
	
	
	
}
