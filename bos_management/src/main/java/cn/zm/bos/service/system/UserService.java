package cn.zm.bos.service.system;

import java.util.List;

import cn.zm.bos.domain.system.User;

public interface UserService {

	//根据用户名查询用户信息
	User findByUsername(String username);
	
	//查询所有的用户
	List<User> findAll();

	//保存用户
	void save(User user, String[] roleIds);

}
