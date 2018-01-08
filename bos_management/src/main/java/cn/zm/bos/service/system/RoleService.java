package cn.zm.bos.service.system;

import java.util.List;

import cn.zm.bos.domain.system.Role;
import cn.zm.bos.domain.system.User;

public interface RoleService {

	//根据用户查询角色集合
	List<Role> findByUser(User user);

	//查询所有的角色
	List<Role> findAll();

	//保存角色
	void save(Role role, String[] permissionIds, String menuIds);

}
