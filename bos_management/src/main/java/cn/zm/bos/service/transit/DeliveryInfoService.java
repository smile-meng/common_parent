package cn.zm.bos.service.transit;

import cn.zm.bos.domain.transit.DeliveryInfo;

public interface DeliveryInfoService {

	void save(DeliveryInfo deliveryInfo, String transitInfoId);

}
