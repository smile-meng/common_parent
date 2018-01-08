package cn.zm.bos.dao.system;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.zm.bos.domain.system.Menu;

public interface MenuDao extends JpaRepository<Menu, Integer>{

	@Query("from Menu m inner join fetch m.roles r inner join fetch r.users u where u.id = ? order by m.priority ")
	List<Menu> findByUser(Integer id);

}
