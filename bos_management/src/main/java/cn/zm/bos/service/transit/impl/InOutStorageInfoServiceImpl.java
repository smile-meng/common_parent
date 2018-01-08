package cn.zm.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.transit.InOutStorageInfoDao;
import cn.zm.bos.dao.transit.TransitInfoDao;
import cn.zm.bos.domain.transit.InOutStorageInfo;
import cn.zm.bos.domain.transit.TransitInfo;
import cn.zm.bos.service.transit.InOutStorageInfoService;

@Service
@Transactional
public class InOutStorageInfoServiceImpl implements InOutStorageInfoService {

	@Autowired
	private InOutStorageInfoDao inOutStorageInfoDao;
	
	@Autowired
	private TransitInfoDao transitInfoDao;
	
	@Override
	public void save(InOutStorageInfo inOutStorageInfo, String transitInfoId) {
		//保存出入库信息
		inOutStorageInfoDao.save(inOutStorageInfo);
		
		//查询transitInfo
		TransitInfo transitInfo = transitInfoDao.findOne(Integer.parseInt(transitInfoId));
		
		//关联运输信息与出入库
		transitInfo.getInOutStorageInfos().add(inOutStorageInfo);
		
		if(inOutStorageInfo.getOperation().equals("到达网点")){
			transitInfo.setStatus("到达网点");
			//更新网点地址,显示配送路径
			transitInfo.setOutletAddress(inOutStorageInfo.getAddress());
		}
		
	}

}
