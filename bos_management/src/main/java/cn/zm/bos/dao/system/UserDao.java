package cn.zm.bos.dao.system;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.zm.bos.domain.system.User;

public interface UserDao  extends JpaRepository<User, Integer>{

	User findByUsername(String username);

}
