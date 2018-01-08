package cn.zm.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.zm.bos.domain.base.TakeTime;

public interface TakeTimeService {

	void save(TakeTime model);

	Page<TakeTime> pageQuery(Pageable pageable);

	void delBatch(String[] idStr);

	List<TakeTime> findAll();

}
