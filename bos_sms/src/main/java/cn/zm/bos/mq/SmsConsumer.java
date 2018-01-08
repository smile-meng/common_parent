package cn.zm.bos.mq;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service("smsConsumer")
public class SmsConsumer implements MessageListener {

	@Override
	public void onMessage(Message message, byte[] pattern) {
		MapMessage mapMessage = (MapMessage) message;
		try {
			// 调用SMS服务发生短信
			// String result = SmsUtils.sendSmsByHTTP(model.getTelephone(),
			// msg);
			String result = "000/xxxx";
			if (result.startsWith("000")) {
				// 发送成功
				System.out.println("发送短信成功，手机号："
						+ mapMessage.getString("telephone") + ",验证码："
						+ mapMessage.getString("msg"));
			} else {
				// 发生失败
				throw new RuntimeException("短信发送失败，信息码：" + result);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
