package cn.zm.bos.syncindex.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.zm.bos.service.take_delivery.WayBillService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SyncIndexTest {

	@Autowired
	private WayBillService wayBillService;

	@Test
	public void syscIndex() {
		wayBillService.syncIndex();
	}
}
