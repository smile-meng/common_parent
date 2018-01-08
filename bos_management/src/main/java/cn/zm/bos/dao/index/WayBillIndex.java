package cn.zm.bos.dao.index;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import cn.zm.bos.domain.take_delivery.WayBill;

public interface WayBillIndex extends ElasticsearchRepository<WayBill, Integer>{

}
