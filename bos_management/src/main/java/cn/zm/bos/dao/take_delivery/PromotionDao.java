package cn.zm.bos.dao.take_delivery;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.zm.bos.domain.take_delivery.Promotion;

public interface PromotionDao extends JpaRepository<Promotion, Integer> {


	public Promotion findById(Integer id);

	@Query("update Promotion set status = '2' where endDate<? and status = '1'")
	@Modifying
	public void updateStatus(Date now);

}
