package cn.zm.bos.web.action.system;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.domain.system.Menu;
import cn.zm.bos.domain.system.User;
import cn.zm.bos.service.system.MenuService;
import cn.zm.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class MenuAction extends BaseAction<Menu> {

	@Autowired
	private MenuService menuService;

	@Action(value = "menu_list", results = { @Result(name = "success", type = "json") })
	public String list() {

		// 调用业务层
		List<Menu> menus = menuService.findAll();
		ActionContext.getContext().getValueStack().push(menus);

		return SUCCESS;
	}

	//保存菜单
	@Action(value = "menu_save", results = { @Result(name = "success", type = "redirect", location = "pages/system/menu.html") })
	public String save() {
		menuService.save(model);
		return SUCCESS;
	}
	
	//基础菜单的展示
	@Action(value="menu_showmenu",results={@Result(name="success",type="json")})
	public String showMenu(){
		//调用业务层，查询当前用户具有菜单列表
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		
		List<Menu> menus = menuService.findByUser(user);
		ActionContext.getContext().getValueStack().push(menus);
		
		return SUCCESS;
	}
	
}
