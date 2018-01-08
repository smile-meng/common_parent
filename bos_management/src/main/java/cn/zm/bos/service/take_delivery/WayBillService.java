package cn.zm.bos.service.take_delivery;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.zm.bos.domain.take_delivery.WayBill;

public interface WayBillService {

	//保存运单
	void save(WayBill wayBill);

	//分页
	Page<WayBill> findPageData(WayBill wayBill, Pageable pageable);

	//根据运单号查询运单详情
	WayBill findByWayBillNum(String wayBillNum);
	
	//同步索引
	void syncIndex();

	//根据条件查询运单
	List<WayBill> findWayBills(WayBill wayBill);

}
