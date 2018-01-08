package cn.zm.bos.service.take_delivery.impl;

import java.util.Date;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zm.bos.dao.base.AreaDao;
import cn.zm.bos.dao.base.FixedAreaDao;
import cn.zm.bos.dao.take_delivery.OrderDao;
import cn.zm.bos.dao.take_delivery.WorkBillDao;
import cn.zm.bos.domain.base.Area;
import cn.zm.bos.domain.base.Courier;
import cn.zm.bos.domain.base.FixedArea;
import cn.zm.bos.domain.base.SubArea;
import cn.zm.bos.domain.constant.Constants;
import cn.zm.bos.domain.take_delivery.Order;
import cn.zm.bos.domain.take_delivery.WorkBill;
import cn.zm.bos.service.take_delivery.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private FixedAreaDao fixedAreaDao;
	
	@Autowired
	private WorkBillDao workBillDao;
	
//	@Autowired
//	private AreaDao areaDao;
	
	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;
	
	@Override
	public void saveOrder(Order order) {

		order.setOrderNum(UUID.randomUUID().toString());// 设置订单号
		order.setOrderTime(new Date());// 设置下单时间
		order.setStatus("1");	//待取件

		// 自动分单逻辑，基于crm地址完全匹配，获取定区，匹配快递员
		String fixedAreaId = WebClient
				.create(Constants.CRM_MANAGEMENT_URL
						+ "/services/customerService/customer/findFixedAreaIdByAddress?address=?"
						+ order.getSendAddress())
				.accept(MediaType.APPLICATION_JSON).get(String.class);
		if (fixedAreaId != null) {
			FixedArea fixedArea = fixedAreaDao.findOne(fixedAreaId);
			Courier courier = fixedArea.getCouriers().iterator().next();
			if (courier != null) {
				// 自动分单成功
				System.out.println("自动分单成功---------");
				saveOrder(order, courier);

				// 生成工单，发送短信
				generateWorkBill(order);

				return;
			}
		}

		// 自动分单逻辑，根据省市区,查询分区关键字，匹配地址，基于分区实现自动分单，匹配快递员
		Area sendArea = order.getSendArea();
//		Area recArea = order.getRecArea();
//		Area persistArea = areaDao.findByProvinceAndCityAndDistrict(sendArea.getProvince(), sendArea.getCity(), sendArea.getDistrict());
//		Area rec = areaDao.findByProvinceAndCityAndDistrict(recArea.getProvince(), recArea.getCity(), recArea.getDistrict());
		for (SubArea subArea : sendArea.getSubareas()) {
//			subArea.setArea(null);
			// 当前客户的下单地址 是否包含分区关键字
			if (order.getSendAddress().contains(subArea.getKeyWords())) {
				// 查找分区，找到定区，找到快递员
				Courier courier = subArea.getFixedArea().getCouriers()
						.iterator().next();
				if (courier != null) {
					// 自动分单成功
//					order.setSendArea(persistArea);
//					order.setRecArea(rec);
					System.out.println("自动分单成功---------");
					saveOrder(order, courier);

					// 生成工单，发送短信
					generateWorkBill(order);
					return;

				}
			}

			// 当前客户的下单地址 是否包含分区辅助关键字
			if (order.getSendAddress().contains(subArea.getAssistKeyWords())) {
				// 查找分区，找到定区，找到快递员
				Courier courier = subArea.getFixedArea().getCouriers()
						.iterator().next();
				if (courier != null) {
					// 自动分单成功
//					order.setSendArea(persistArea);
//					order.setRecArea(rec);
					System.out.println("自动分单成功---------");
					saveOrder(order, courier);

					// 生成工单，发送短信
					generateWorkBill(order);
					return;

				}
			}
		}
		
		//人工分单
		order.setOrderType("2");
		orderDao.save(order);
		generateWorkBill(order);
	}

	// 生成工单，发送短信
	private void generateWorkBill(final Order order) {
		//生成工单
		WorkBill workBill = new WorkBill();
		workBill.setType("新");
		workBill.setPickstate("新单");
		workBill.setBuildtime(new Date());
		workBill.setRemark(order.getRemark());
		final String smsNumber = RandomStringUtils.randomNumeric(4);
		workBill.setSmsNumber(smsNumber);//短信序号
		workBill.setOrder(order);
		workBill.setCourier(order.getCourier());
		workBillDao.save(workBill);
		
		//发送短信
		//调用MQ服务，发送一条消息
		jmsTemplate.send("bos_sms",new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", order.getCourier().getTelephone());
				mapMessage.setString("msg", "短信序号："+smsNumber+",取件地址："+order.getSendAddress()
						+",联系人："+order.getSendName()+",手机："
						+order.getSendMobile()+"，快递员捎话："+order.getSendMobileMsg());
				return mapMessage;
			}
		});
		workBill.setPickstate("已通知");
	}

	// 自动分单
	private void saveOrder(Order order, Courier courier) {
		// 关联快递员
		order.setCourier(courier);
		// 设置自动分单
		order.setOrderType("1");
		// 保存订单
		orderDao.save(order);
	}

	@Override
	public Order findByOrderNum(String orderNum) {
		return orderDao.findByOrderNum(orderNum);
	}
}
