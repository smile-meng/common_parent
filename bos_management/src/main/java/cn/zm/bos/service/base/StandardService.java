package cn.zm.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.zm.bos.domain.base.Standard;

public interface StandardService {

	void save(Standard standard);

	Page<Standard> findPageData(Pageable pageable);

	List<Standard> findAll();

}
