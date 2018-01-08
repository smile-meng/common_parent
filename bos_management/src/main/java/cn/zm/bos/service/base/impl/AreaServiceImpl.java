package cn.zm.bos.service.base.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.AreaDao;
import cn.zm.bos.domain.base.Area;
import cn.zm.bos.domain.base.SubArea;
import cn.zm.bos.service.base.AreaService;

@Service
@Transactional
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaDao areaDao;

	@Override
	public void save(Area area) {
		areaDao.save(area);
	}

	@Override
	public void delBatch(String[] idStrs) {
		for (String id : idStrs) {
			areaDao.delete(id);
		}
	}

	@Override
	public void batchImport(List<Area> list) {
		areaDao.save(list);
	}

	@Override
	public Page<Area> pageQuery(Specification<Area> specification,
			Pageable pageable) {
		return areaDao.findAll(specification, pageable);
	}

	@Override
	public List<String> findAllProvince() {
		return areaDao.findAllProvince();
	}

	@Override
	public List<String> findCityByProvince(String province) {
		return areaDao.findCityByProvince(province);
	}

	@Override
	public List<Area> findDistrictByCity(String city) {
		return areaDao.findDistrictByCity(city);
	}
	
	@Override
	public List<Area> findAll() {
		return areaDao.findAll();
	}

	@Override
	public Area findByProvinceAndCityAndDistrict(String province, String city,
			String district) {
		Area area = areaDao.findByProvinceAndCityAndDistrict(province,city,district);
//		Set<SubArea> subareas = area.getSubareas();
//		for (SubArea subArea : subareas) {
//			subArea.setArea(null);
//		}
		return area;
	}

	

}
