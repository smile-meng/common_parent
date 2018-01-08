package cn.zm.bos.dao.transit;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.zm.bos.domain.transit.TransitInfo;

public interface TransitInfoDao extends JpaRepository<TransitInfo, Integer>{

}
