package com.regaltec.asip.manager.api.util;
import java.lang.reflect.Array;
import java.util.Date;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.regaltec.asip.manager.api.client.jms.vo.MsgBean;
/**
 * <p>JMS工具类</p>
 * <p>2011-2-28 9:45:07</p>
 * @author 戈亮锋
 */
public class JmsMessageUtil {

	private static Logger log = LoggerFactory.getLogger(JmsMessageUtil.class);

	/**
	 * 是否为Topic
	 * @param message
	 * @return  boolean
	 * @throws JMSException
	 */
	public static boolean isTopic(Message message)  throws  JMSException{
		String destName = message.getJMSDestination().toString();
		if(destName.startsWith("topic://")){
			return true;
		}
		return false;
	}
	
	/**
	 * JMS Message转换为XML字符串
	 * @param msg Message
	 * @return XML String
	 * @throws JMSException
	 */
	public static String toXML(Message msg)throws JMSException {
		StringBuilder sb =new StringBuilder();
		sb.append("\n<root>\n");
		sb.append("\t<jmsMessageID>").append(msg.getJMSMessageID()).append("</jmsMessageID>\n");
		sb.append("\t<jmsType>").append(msg.getJMSType()).append("</jmsType>\n");
		sb.append("\t<jmsDeliveryMode>").append(msg.getJMSDeliveryMode()).append("</jmsDeliveryMode>\n");
		sb.append("\t<jmsPriority>").append(msg.getJMSPriority()).append("</jmsPriority>\n");
		sb.append("\t<jmsCorrelationID>").append(msg.getJMSCorrelationID()).append("</jmsCorrelationID>\n");
		sb.append("\t<jmsRedelivered>").append(msg.getJMSRedelivered()).append("</jmsRedelivered>\n");
		sb.append("\t<jmsExpiration>").append(msg.getJMSExpiration()).append("</jmsExpiration>\n");
		sb.append("\t<jmsTimestamp>").append(DateUtil.toStrFromLongDate(msg.getJMSTimestamp())).append("</jmsTimestamp>\n");	
		sb.append("\t<jmsDestination>").append(msg.getJMSDestination()).append("</jmsDestination>\n");	
		sb.append("\t<jmsReplyTo>").append(msg.getJMSReplyTo()).append("</jmsReplyTo>\n");	
		sb.append("\t<msgInfo>");
		if(msg instanceof TextMessage){
			    sb.append(((TextMessage)msg).getText());
		  }else if (msg instanceof ObjectMessage){//ActiveMQObjectMessage
			  ActiveMQObjectMessage  mqObj = (ActiveMQObjectMessage) msg;
			  Object msgBody = mqObj.getObject();
			   if (ObjectUtil.isArray(msgBody)) { 
				   log.info("receive a ObjectMessage!And it is a Array, it's length=" + Array.getLength(msgBody));
				   Object[] msgArray = (Object[])msgBody;
				   for (int i = 0; i < msgArray.length; i++) {
						  log.info("####["+i+"]("+msgArray[i].getClass().getName()+"), body="+msgArray[i]);
					   if(msgArray[i] instanceof Boolean){
		                	
		                }else if (msgArray[i] instanceof Byte ) {
							
						}else if (msgArray[i] instanceof Short) {
							
						}else if (msgArray[i] instanceof Character) {
							
						}else if (msgArray[i] instanceof Integer) {
							
						}else if (msgArray[i] instanceof Long) {
							
						}else if (msgArray[i] instanceof Float) {
							
						}else if (msgArray[i] instanceof Double) {
							
						}else if (msgArray[i] instanceof String) {

						}else if (msgArray[i] instanceof byte[] ) {
							
						}else if (msgArray[i] instanceof Date ) {
							
						}
				   }
			   }else{
			    	log.info("receive a ObjectMessage!And it is not a array!!!");
			    	if (msgBody instanceof MsgBean ) {
			    		sb.append((MsgBean)msgBody );	
					}else{
						log.info("receive a ObjectMessage!And it is not instanceof MsgBean!!!");
					}
			   }
		  }else if (msg instanceof MapMessage){
			    sb.append("收到[MapMessage]...");				
		  }else if (msg instanceof StreamMessage){
			    sb.append("收到[StreamMessage]...");				
		  }else if (msg instanceof BytesMessage){
			    sb.append("收到[BytesMessage]...");				
		  }else{
			    sb.append("收到JMS消息,类型未知!!!");				
		  }
		sb.append("</msgInfo>\n");
		sb.append("</root>\n");
		return sb.toString();
	}

}
