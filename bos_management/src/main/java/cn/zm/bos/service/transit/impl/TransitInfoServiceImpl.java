package cn.zm.bos.service.transit.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.take_delivery.WayBillDao;
import cn.zm.bos.dao.transit.TransitInfoDao;
import cn.zm.bos.domain.take_delivery.WayBill;
import cn.zm.bos.domain.transit.TransitInfo;
import cn.zm.bos.index.WayBillIndex;
import cn.zm.bos.service.transit.TransitInfoService;

@Service
@Transactional
public class TransitInfoServiceImpl implements TransitInfoService{

	@Autowired
	private WayBillDao wayBillDao;
	
	@Autowired
	private TransitInfoDao transitInfoDao;
	
	@Autowired
	private WayBillIndex wayBillIndex;
	
	@Override
	public void save(String wayBillIds) {
		if(StringUtils.isNoneBlank(wayBillIds)){
			//处理运单
			String[] ids = wayBillIds.split(",");
			for (String wayBillId : ids) {
				WayBill wayBill = wayBillDao.findOne(Integer.parseInt(wayBillId));
				//判断运单状态是否为待发货
				if(wayBill.getSignStatus() == 1){
					//保存TransitInfo信息
					TransitInfo transitInfo = new TransitInfo();
					transitInfo.setWayBill(wayBill);
					transitInfo.setStatus("出入库中转");
					transitInfoDao.save(transitInfo);
					
					//更改运单状态
					wayBill.setSignStatus(2);//派送中
					//同步索引库
					wayBillIndex.save(wayBill);
				}
			}
		}
	}

	@Override
	public Page<TransitInfo> findAll(Pageable pageable) {
		return transitInfoDao.findAll(pageable);
	}

}
