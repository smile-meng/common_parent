package cn.zm.bos.dao.take_delivery;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.zm.bos.domain.take_delivery.WayBill;

public interface WayBillDao extends JpaRepository<WayBill, Integer>{

	WayBill findByWayBillNum(String wayBillNum);

}
