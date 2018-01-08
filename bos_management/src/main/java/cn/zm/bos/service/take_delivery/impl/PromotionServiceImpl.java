package cn.zm.bos.service.take_delivery.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.take_delivery.PromotionDao;
import cn.zm.bos.domain.page.PageBean;
import cn.zm.bos.domain.take_delivery.Promotion;
import cn.zm.bos.service.take_delivery.PromotionService;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

	@Autowired
	private PromotionDao promotionDao;
	
	@Override
	public void save(Promotion promotion) {
		promotionDao.save(promotion);
	}

	@Override
	public Page<Promotion> findPageData(Pageable pageable) {
		return promotionDao.findAll(pageable);
	}

	@Override
	public PageBean<Promotion> pageQuery(int page, int rows) {
		//构造分页对象
		Pageable pageable = new PageRequest(page-1, rows);
		Page<Promotion> pageData = promotionDao.findAll(pageable);
		
		//将分页数据封装到pageBean
		PageBean<Promotion> pageBean = new PageBean<Promotion>();
		pageBean.setPageData(pageData.getContent());
		pageBean.setTotalCount(pageData.getTotalElements());

		return pageBean;
	}

	@Override
	public Promotion findById(Integer id) {
		return promotionDao.findById(id);
	}

	@Override
	public void updateStatus(Date date) {
		promotionDao.updateStatus(date);
	}

}
