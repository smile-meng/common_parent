package cn.zm.bos.dao.take_delivery;


import org.springframework.data.jpa.repository.JpaRepository;

import cn.zm.bos.domain.take_delivery.Order;


public interface OrderDao extends JpaRepository<Order, Integer> {

	Order findByOrderNum(String orderNum);


}
