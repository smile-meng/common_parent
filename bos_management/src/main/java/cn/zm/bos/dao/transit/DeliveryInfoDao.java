package cn.zm.bos.dao.transit;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.zm.bos.domain.transit.DeliveryInfo;

public interface DeliveryInfoDao extends JpaRepository<DeliveryInfo, Integer>{

}
