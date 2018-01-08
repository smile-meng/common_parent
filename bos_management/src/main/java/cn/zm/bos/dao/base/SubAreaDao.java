package cn.zm.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.zm.bos.domain.base.SubArea;

public interface SubAreaDao extends JpaRepository<SubArea, String>,JpaSpecificationExecutor<SubArea> {

}
