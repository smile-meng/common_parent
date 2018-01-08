package cn.zm.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.system.PermissionDao;
import cn.zm.bos.domain.system.Permission;
import cn.zm.bos.domain.system.User;
import cn.zm.bos.service.system.PermissionService;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionDao permissionDao;

	@Override
	public List<Permission> findByUser(User user) {
		if (user.getUsername().equals("admin")) {
			return permissionDao.findAll();
		} else {
			
			return permissionDao.findByUser(user.getId());
		}
	}

	@Override
	public List<Permission> findAll() {
		return permissionDao.findAll();
	}

	@Override
	public void save(Permission permission) {
		permissionDao.save(permission);
	}

}
