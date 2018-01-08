package cn.zm.bos.dao.system;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.zm.bos.domain.system.Permission;

public interface PermissionDao extends JpaRepository<Permission, Integer> {

	@Query("from Permission p inner join fetch p.roles r inner join fetch r.users u where u.id=? ")
	List<Permission> findByUser(int id);

}
