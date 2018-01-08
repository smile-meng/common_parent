package cn.zm.bos.service.transit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.zm.bos.domain.transit.TransitInfo;

public interface TransitInfoService {

	//保存运单配送信息
	void save(String wayBillIds);

	//分页查询
	Page<TransitInfo> findAll(Pageable pageable);

}
