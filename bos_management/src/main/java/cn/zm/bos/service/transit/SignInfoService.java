package cn.zm.bos.service.transit;

import cn.zm.bos.domain.transit.SignInfo;

public interface SignInfoService {

	//保存签收录入信息
	void save(SignInfo signInfo, String transitInfoId);

}
