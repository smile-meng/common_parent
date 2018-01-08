package cn.zm.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.SubAreaDao;
import cn.zm.bos.domain.base.SubArea;
import cn.zm.bos.service.base.SubAreaService;

@Service
@Transactional
public class SubAreaServiceImpl implements SubAreaService {

	@Autowired
	private SubAreaDao subAreaDao;
	
	@Override
	public void save(SubArea subArea) {
		subAreaDao.save(subArea);
	}

	@Override
	public Page<SubArea> pageQuery(Specification<SubArea> specification,
			Pageable pageable) {
		return subAreaDao.findAll(specification, pageable);
	}

	@Override
	public void delBatch(String[] idStr) {
		for (String id : idStr) {
			subAreaDao.delete(id);
		}
	}

	@Override
	public void batchImport(List<SubArea> list) {
		subAreaDao.save(list);
	}

	@Override
	public List<SubArea> findAll() {
		return subAreaDao.findAll();
	}

}
