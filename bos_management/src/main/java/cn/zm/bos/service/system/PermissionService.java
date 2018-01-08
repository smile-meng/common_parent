package cn.zm.bos.service.system;

import java.util.List;

import cn.zm.bos.domain.system.Permission;
import cn.zm.bos.domain.system.User;

public interface PermissionService {

	//根据用户查询权限集合
	List<Permission> findByUser(User user);

	//查询所有的权限
	List<Permission> findAll();

	//保存权限
	void save(Permission permission);

}
