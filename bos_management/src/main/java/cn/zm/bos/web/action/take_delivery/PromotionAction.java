package cn.zm.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
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
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.domain.take_delivery.Promotion;
import cn.zm.bos.service.take_delivery.PromotionService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {

	private File titleImgFile;// 宣传图

	private String titleImgFileFileName;

	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}

	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}

	@Autowired
	private PromotionService promotionService;

	@Action(value = "promotion_save", results = { @Result(name = "success", type = "redirect", location = "./pages/take_delivery/promotion.html") })
	public String save() throws Exception {
		// 宣传图上传，在数据表保存宣传图路径
		String savePath = ServletActionContext.getServletContext().getRealPath(
				"/upload/");
		String saveUrl = ServletActionContext.getRequest().getContextPath()
				+ "/upload/";

		// 生成随机图片名
		UUID uuid = UUID.randomUUID();
		String imgName = titleImgFileFileName.substring(titleImgFileFileName
				.lastIndexOf("."));
		String uuidFileName = uuid + imgName;
		// 保存图片（绝对路径）
		FileUtils.copyFile(titleImgFile, new File(savePath, uuidFileName));

		// 将保存路径 相对工程web访问路径，保存model中
		model.setTitleImg(saveUrl + uuidFileName);

		// 调用业务成，完成活动任务数据保存
		promotionService.save(model);

		return SUCCESS;
	}
	
	@Action(value="promotion_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//构造分页查询参数
		Pageable pageable = new PageRequest(page-1, rows);
		//调用业务层，完成查询
		Page<Promotion> pageData = promotionService.findPageData(pageable);
		//压入值栈
		pushPageDateToValueStack(pageData);
		
		return SUCCESS;
	}
}
