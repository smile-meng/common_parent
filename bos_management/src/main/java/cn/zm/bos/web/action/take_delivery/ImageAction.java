package cn.zm.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.web.action.common.BaseAction;

/**
 * 处理kindeditor图片上传 、管理功能
 * 
 * @author zhaomeng
 * 
 */
@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ImageAction extends BaseAction<Object> {

	private File imgFile; // 图片文件
	private String imgFileFileName; // 文件名称
	private String imgFileContentType;// 文件类型

	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}

	public void setImgFileFileName(String imgFileFileName) {
		this.imgFileFileName = imgFileFileName;
	}

	public void setImgFileContentType(String imgFileContentType) {
		this.imgFileContentType = imgFileContentType;
	}

	@Action(value = "image_upload", results = { @Result(name = "success", type = "json") })
	public String upload() throws IOException {

		// 获得绝对路径
		String saveRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/");

		// 获得相对路径
		String saveUrl = ServletActionContext.getRequest().getContextPath()
				+ "/upload/";
		// 生成随机图片名
		UUID uuid = UUID.randomUUID();
		String imgName = imgFileFileName.substring(imgFileFileName
				.lastIndexOf("."));
		String uuidFileName = uuid + imgName;
		// 保存图片（绝对路径）
		FileUtils.copyFile(imgFile, new File(saveRealPath, uuidFileName));
		// 通知浏览器文件上传成功
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", 0);
		map.put("url", saveUrl + uuidFileName);// 返回相对路径
		ActionContext.getContext().getValueStack().push(map);

		return SUCCESS;
	}

	@Action(value = "image_manage", results = { @Result(name = "success", type = "json") })
	public String manage() throws Exception {
		// 获得绝对路径
		String rootPath = ServletActionContext.getServletContext().getRealPath(
				"/upload/");

		// 获得相对路径
		String rootUrl = ServletActionContext.getRequest().getContextPath()
				+ "/upload/";

		// 图片扩展名
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp" };

		// 当前上传目录
		File currentPathFile = new File(rootPath);

		// 遍历目录取的文件信息
		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
		if (currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					map.put("is_dir", true);
					map.put("has_file", (file.listFiles() != null));
					map.put("filesize", 0L);
					map.put("is_photo", false);
					map.put("filetype", "");
				} else if (file.isFile()) {
					String fileExt = fileName.substring(
							fileName.lastIndexOf(".") + 1).toLowerCase();
					map.put("is_dir", false);
					map.put("has_file", false);
					map.put("filesize", file.length());
					map.put("is_photo", Arrays.<String> asList(fileTypes)
							.contains(fileExt));
					map.put("filetype", fileExt);
				}
				map.put("filename", fileName);
				map.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(file.lastModified()));
				fileList.add(map);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("moveup_dir_path", "");
		result.put("current_dir_path", rootPath);
		result.put("current_url", rootUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);

		ActionContext.getContext().getValueStack().push(result);

		return SUCCESS;
	}

}
