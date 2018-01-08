package cn.zm.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.VehicleDao;
import cn.zm.bos.domain.base.Vehicle;
import cn.zm.bos.service.base.VehicleService;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleDao vehicleDao;
	
	@Override
	public void save(Vehicle vehicle) {
		vehicleDao.save(vehicle);
	}

}
