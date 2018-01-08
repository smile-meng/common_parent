package cn.zm.bos.service.system.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.system.MenuDao;
import cn.zm.bos.dao.system.PermissionDao;
import cn.zm.bos.dao.system.RoleDao;
import cn.zm.bos.domain.system.Menu;
import cn.zm.bos.domain.system.Permission;
import cn.zm.bos.domain.system.Role;
import cn.zm.bos.domain.system.User;
import cn.zm.bos.service.system.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PermissionDao permissionDao;
	
	@Autowired
	private MenuDao menuDao;
	
	@Override
	public List<Role> findByUser(User user) {
		// 基于用户查询角色
		// admin具有所有角色
		if (user.getUsername().equals("admin")) {
			return roleDao.findAll();
		} else {
			return roleDao.findByUser(user.getId());
		}
	}

	@Override
	public List<Role> findAll() {
		return roleDao.findAll();
	}

	@Override
	public void save(Role role, String[] permissionIds, String menuIds) {
		roleDao.save(role);
		//关联权限
		if(permissionIds!=null){
			for (String permissionId : permissionIds) {
				Permission permission = permissionDao.findOne(Integer.parseInt(permissionId));
				role.getPermissions().add(permission);
			}
		}
		//关联菜单
		if(StringUtils.isNoneBlank(menuIds)){
			String[] mIds = menuIds.split(",");
			for (String menuId : mIds) {
				Menu menu = menuDao.findOne(Integer.parseInt(menuId));
				role.getMenus().add(menu);
			}
		}
	}

}
