package cn.zm.bos.service.base.impl;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionContext;

import cn.zm.bos.dao.base.CourierDao;
import cn.zm.bos.dao.base.FixedAreaDao;
import cn.zm.bos.dao.base.TakeTimeDao;
import cn.zm.bos.domain.base.Courier;
import cn.zm.bos.domain.base.FixedArea;
import cn.zm.bos.domain.base.TakeTime;
import cn.zm.bos.service.base.FixedAreaService;
import cn.zm.crm.domain.Customer;
@Service
@Transactional
public class FixedAreaServiceImpl implements FixedAreaService {

	@Autowired
	private FixedAreaDao fixedAreaDao;
	
	//添加或修改区域
	@Override
	public void save(FixedArea fixedArea) {
		fixedAreaDao.save(fixedArea);
	}

	//有无条件查询区域
	@Override
	public Page<FixedArea> pageQuery(Specification<FixedArea> specification,
			Pageable pageable) {
		return fixedAreaDao.findAll(specification, pageable);
	}

	//批量删除区域
	@Override
	public void delBatch(String[] fixedAreaIds) {
		for (String id : fixedAreaIds) {
			fixedAreaDao.delete(id);
		}
	}
	
	@Autowired
	private CourierDao courierDao;
	
	@Autowired
	private TakeTimeDao takeTimeDao;
	
	//关联快递员到定区
	@Override
	public void associationCourierToFixedArea(FixedArea model,
			Integer courierId, Integer takeTimeId) {
		FixedArea fixedArea = fixedAreaDao.findOne(model.getId());
		Courier courier = courierDao.findOne(courierId);
		TakeTime takeTime = takeTimeDao.findOne(takeTimeId);
		
		//关联快递员到 定区
		fixedArea.getCouriers().add(courier);
		
		//关联收派时间到快递员
		courier.setTakeTime(takeTime);
	}

	@Override
	public List<FixedArea> findAllFixedArea() {
		return fixedAreaDao.findAll();
	}

}
