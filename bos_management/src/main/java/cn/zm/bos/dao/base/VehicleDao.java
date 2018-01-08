package cn.zm.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.zm.bos.domain.base.Vehicle;

public interface VehicleDao extends JpaRepository<Vehicle, Integer>,JpaSpecificationExecutor<Vehicle> {

}
