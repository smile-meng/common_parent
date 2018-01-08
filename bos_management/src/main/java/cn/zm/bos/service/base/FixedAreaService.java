package cn.zm.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.zm.bos.domain.base.FixedArea;

public interface FixedAreaService {

	void save(FixedArea fixedArea);

	Page<FixedArea> pageQuery(Specification<FixedArea> specification,
			Pageable pageable);

	void delBatch(String[] fixedAreaIds);

	void associationCourierToFixedArea(FixedArea model, Integer courierId,
			Integer takeTimeId);

	List<FixedArea> findAllFixedArea();


}
