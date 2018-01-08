package cn.zm.bos.web.action.system;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.domain.system.Role;
import cn.zm.bos.service.system.RoleService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class RoleAction extends BaseAction<Role> {

	@Autowired
	private RoleService roleService;

	@Action(value = "role_list", results = { @Result(name = "success", type = "json") })
	public String list() {
		// 调用业务层，查询所有角色
		List<Role> roles = roleService.findAll();
		ActionContext.getContext().getValueStack().push(roles);
		return SUCCESS;
	}

	private String menuIds;

	private String[] permissionIds;

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}

	public void setPermissionIds(String[] permissionIds) {
		this.permissionIds = permissionIds;
	}

	@Action(value = "role_save", results = { @Result(name = "success", type = "redirect", location = "pages/system/role.html") })
	public String save() {
		roleService.save(model, permissionIds, menuIds);
		return SUCCESS;
	}
}
