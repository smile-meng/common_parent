package cn.zm.bos.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import cn.zm.bos.service.take_delivery.PromotionService;

public class PromotionJob implements Job{
	
	@Autowired
	private PromotionService promotionService;
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("活动过期处理。。。");
		//每分钟执行一次，当前时间大于promotion数据表endDate ,活动已经过期，设置status='2'
		promotionService.updateStatus(new Date());
	}

}
