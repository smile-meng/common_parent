package cn.zm.bos.service.transit;

import cn.zm.bos.domain.transit.InOutStorageInfo;

public interface InOutStorageInfoService {

	//保存出入库信息
	void save(InOutStorageInfo inOutStorageInfo, String transitInfoId);

}
