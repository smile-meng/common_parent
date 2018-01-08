package cn.zm.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.TakeTimeDao;
import cn.zm.bos.domain.base.TakeTime;
import cn.zm.bos.service.base.TakeTimeService;

@Service
@Transactional
public class TakeTimeServiceImpl implements TakeTimeService{

	@Autowired
	private TakeTimeDao takeTimeDao;
	
	@Override
	public void save(TakeTime model) {
		takeTimeDao.save(model);	
	}

	@Override
	public Page<TakeTime> pageQuery(Pageable pageable) {
		return takeTimeDao.findAll(pageable);
	}

	@Override
	public void delBatch(String[] idStr) {
		for (String id : idStr) {
			takeTimeDao.delete(Integer.parseInt(id));
		}
	}

	@Override
	public List<TakeTime> findAll() {
		return takeTimeDao.findAll();
	}

}
