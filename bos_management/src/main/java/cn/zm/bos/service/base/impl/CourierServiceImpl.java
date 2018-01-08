package cn.zm.bos.service.base.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.CourierDao;
import cn.zm.bos.domain.base.Courier;
import cn.zm.bos.service.base.CourierService;
@Service
@Transactional
public class CourierServiceImpl implements CourierService {

	@Autowired
	private CourierDao courierDao;
	
	@Override
	@RequiresPermissions("courier_add")
	public void save(Courier courier) {
		courierDao.save(courier);
	}

	@Override
	public Page<Courier> findAll(Specification<Courier> specification,
			Pageable pageable) {
		return courierDao.findAll(specification,pageable);
	}

	@Override
	public void delBatch(String[] idStrs) {
		for (String id : idStrs) {
			courierDao.delUpdateDeltag(Integer.parseInt(id));
		}
	}

	@Override
	public void addBatch(String[] idStrs) {
		for (String id : idStrs) {
			courierDao.addUpdateDeltag(Integer.parseInt(id));
		}
	}

	//条件查询
	@Override
	public List<Courier> findNoAssociation() {
		
		Specification<Courier> specification = new Specification<Courier>() {
			
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				//查询条件，区域列表为空
				Predicate p = cb.isEmpty(root.get("fixedAreas").as(Set.class));
				
				return p;
			}
		};
		return courierDao.findAll(specification);
	}

}
