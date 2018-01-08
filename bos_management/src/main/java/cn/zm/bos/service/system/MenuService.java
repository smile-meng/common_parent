package cn.zm.bos.service.system;

import java.util.List;

import cn.zm.bos.domain.system.Menu;
import cn.zm.bos.domain.system.User;

public interface MenuService {

	//查询所有菜单信息
	List<Menu> findAll();

	//保存菜单
	void save(Menu menu);

	//根据用户查询菜单
	List<Menu> findByUser(User user);

}
