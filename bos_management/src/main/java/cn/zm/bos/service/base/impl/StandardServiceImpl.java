package cn.zm.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.StandardDao;
import cn.zm.bos.domain.base.Standard;
import cn.zm.bos.service.base.StandardService;

@Service
@Transactional
public class StandardServiceImpl implements StandardService {

	@Autowired
	private StandardDao standardDao;

	@Override
	@CacheEvict(value = "standard", allEntries = true)
	public void save(Standard standard) {
		standardDao.save(standard);
	}

	@Override
	@Cacheable(value="standard",key="#pageable.pageNumber+'_'+#pageable.pageSize")
	public Page<Standard> findPageData(Pageable pageable) {
		return standardDao.findAll(pageable);
	}

	@Override
	@Cacheable("standard")
	public List<Standard> findAll() {
		return standardDao.findAll();
	}
}
