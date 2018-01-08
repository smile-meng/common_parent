package cn.zm.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.zm.bos.domain.base.SubArea;

public interface SubAreaService {

	void save(SubArea subArea);

	Page<SubArea> pageQuery(Specification<SubArea> specification,
			Pageable pageable);

	void delBatch(String[] idStrs);

	void batchImport(List<SubArea> list);

	List<SubArea> findAll();

}
