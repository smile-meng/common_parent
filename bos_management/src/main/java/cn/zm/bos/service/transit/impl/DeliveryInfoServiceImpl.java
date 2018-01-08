package cn.zm.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.transit.DeliveryInfoDao;
import cn.zm.bos.dao.transit.TransitInfoDao;
import cn.zm.bos.domain.transit.DeliveryInfo;
import cn.zm.bos.domain.transit.TransitInfo;
import cn.zm.bos.service.transit.DeliveryInfoService;

@Service
@Transactional
public class DeliveryInfoServiceImpl implements DeliveryInfoService {

	@Autowired
	private DeliveryInfoDao deliveryInfoDao;
	
	@Autowired
	private TransitInfoDao transitInfoDao;
	
	@Override
	public void save(DeliveryInfo deliveryInfo, String transitInfoId) {
		//保存配送信息
		deliveryInfoDao.save(deliveryInfo);
		
		//查询运输信息
		TransitInfo transitInfo = transitInfoDao.findOne(Integer.parseInt(transitInfoId));
		
		//关联配送信息
		transitInfo.setDeliveryInfo(deliveryInfo);
		
		//更改状态
		transitInfo.setStatus("开始配送");
	}

}
