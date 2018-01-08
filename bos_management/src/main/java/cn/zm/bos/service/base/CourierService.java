package cn.zm.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.zm.bos.domain.base.Courier;

public interface CourierService {

	void save(Courier courier);

	Page<Courier> findAll(Specification<Courier> specification, Pageable pageable);

	void delBatch(String[] idStrs);

	void addBatch(String[] idStrs);

	List<Courier> findNoAssociation();

}
