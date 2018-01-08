package cn.zm.bos.dao.transit;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.zm.bos.domain.transit.InOutStorageInfo;

public interface InOutStorageInfoDao extends JpaRepository<InOutStorageInfo, Integer> {

}
