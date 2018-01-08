package cn.zm.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.transit.SignInfoDao;
import cn.zm.bos.dao.transit.TransitInfoDao;
import cn.zm.bos.domain.transit.SignInfo;
import cn.zm.bos.domain.transit.TransitInfo;
import cn.zm.bos.index.WayBillIndex;
import cn.zm.bos.service.transit.SignInfoService;

@Service
@Transactional
public class SignInfoServiceImpl implements SignInfoService {

	@Autowired
	private SignInfoDao signInfoDao;
	
	@Autowired
	private TransitInfoDao transitInfoDao;
	
	@Autowired
	private WayBillIndex wayBillIndex;
	
	@Override
	public void save(SignInfo signInfo, String transitInfoId) {
		//保存签收录入信息
		signInfoDao.save(signInfo);
		
		//关联运输
		TransitInfo transitInfo = transitInfoDao.findOne(Integer.parseInt(transitInfoId));
		
		transitInfo.setSignInfo(signInfo);
		
		//更改状态
		if(signInfo.getSignType().equals("正常")){
			//正常签收
			transitInfo.setStatus("正常签收");
			//更改运单状态
			transitInfo.getWayBill().setSignStatus(3);
			//更改索引库
			wayBillIndex.save(transitInfo.getWayBill());
		}else{
			//异常
			transitInfo.setStatus("异常");
			//更改运单状态
			transitInfo.getWayBill().setSignStatus(4);
			//更改索引库
			wayBillIndex.save(transitInfo.getWayBill());
		}
	}

	
}
