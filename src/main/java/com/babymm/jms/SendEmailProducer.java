package com.babymm.jms;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SendEmailProducer {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Resource
	private Destination queueDestionEmail;

	/**
	 * 发送邮箱激活链接
	 * 
	 * @param text
	 */
	public void sendActiveEmailUrl(final Map<String, String> map) {
		jmsTemplate.send(queueDestionEmail, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {

				String map_Str;
				try {
					map_Str = new ObjectMapper().writeValueAsString(map); // 将map转换成字符串
					return session.createObjectMessage(map_Str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	/**
	 * 发送验证码
	 * 
	 * @param message
	 */
	@Resource
	private Destination queueDestionCode;

	public void sendCode(final Map<String, String> code) {
		jmsTemplate.send(queueDestionCode, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				String code_Str;
				try {
					code_Str = new ObjectMapper().writeValueAsString(code); // 将map转换成字符串
					return session.createObjectMessage(code_Str);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
}
