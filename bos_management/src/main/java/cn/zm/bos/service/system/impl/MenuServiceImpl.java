package cn.zm.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.system.MenuDao;
import cn.zm.bos.domain.system.Menu;
import cn.zm.bos.domain.system.User;
import cn.zm.bos.service.system.MenuService;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuDao menuDao;

	@Override
	public List<Menu> findAll() {
		return menuDao.findAll();
	}

	@Override
	public void save(Menu menu) {
		// 防止用户没有选中 父菜单
		if (menu.getParentMenu() != null && menu.getParentMenu().getId() == 0) {
			menu.setParentMenu(null);
		}
		menuDao.save(menu);
	}

	@Override
	public List<Menu> findByUser(User user) {
		//针对用户显示菜单
		if(user.getUsername().equals("admin")){
			return menuDao.findAll();
		}else{
			return menuDao.findByUser(user.getId());
		}
	}
}
