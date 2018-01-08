package cn.zm.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.system.RoleDao;
import cn.zm.bos.dao.system.UserDao;
import cn.zm.bos.domain.system.Role;
import cn.zm.bos.domain.system.User;
import cn.zm.bos.service.system.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public List<User> findAll() {
		return userDao.findAll();
	}

	@Override
	public void save(User user, String[] roleIds) {
		//保存用户
		userDao.save(user);
		//授予角色
		if(roleIds != null){
			for (String roleId : roleIds) {
				Role role = roleDao.findOne(Integer.parseInt(roleId));
				user.getRoles().add(role);
			}
		}
		
	}
	

}
