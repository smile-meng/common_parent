package cn.zm.bos.web.action.transit;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.zm.bos.domain.transit.InOutStorageInfo;
import cn.zm.bos.service.transit.InOutStorageInfoService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class InOutStorageInfoAction extends BaseAction<InOutStorageInfo> {

	@Autowired
	private InOutStorageInfoService inOutStorageInfoService;

	private String transitInfoId;

	public void setTransitInfoId(String transitInfoId) {
		this.transitInfoId = transitInfoId;
	}

	@Action(value = "inoutstore_save", results = { @Result(name = "success", type = "redirect", location = "pages/transit/transitinfo.html") })
	public String save() {
		//调用业务层，进行保存
		inOutStorageInfoService.save(model,transitInfoId);
		return SUCCESS;
	}
}
