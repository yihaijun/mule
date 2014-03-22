package com.regaltec.asip.manager.api.client.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.regaltec.asip.manager.api.client.jms.vo.MsgBean;
import com.regaltec.asip.manager.api.client.jms.vo.MsgConstant;
import com.regaltec.asip.manager.api.util.JmsMessageUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQConnection;
import javax.jms.*;

/**
 * <p>JMS客户端工具类(发送同步jms消息)</p>
 * <p>2011-2-28 9:41:30</p>
 * @author 戈亮锋
 */
public class JmsClient {
    private static Logger log = LoggerFactory.getLogger(JmsClient.class);
    private String brokerURL;
    private String queueName;
    private static transient ConnectionFactory factory; // 不会被持久化
    private transient Connection connection;
    private transient Session session;
    private transient MessageProducer producer;

    public JmsClient(String brokerURL, String queueName) throws JMSException {
        this.brokerURL = brokerURL;
        this.queueName = queueName;
        factory = new ActiveMQConnectionFactory(this.brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); // 是否保存消息
        // producer.setTimeToLive(0); //0： 表示永不过期 ,单位：ms
        // producer.setPriority(9); //0-9, 9最大,4:缺省值
    }

    public void close() throws JMSException {
        if (producer != null) {
            producer.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * 
     * @param msgBean   消息Bean
     * @throws JMSException
     */
    public Message sendMessage(MsgBean msgBean) throws JMSException {
        return sendMessage(msgBean, -1);
    }

    /**
     * 
     * @param msgBean   消息Bean
     * @param timeOut   超时(单位:ms)
     * @throws JMSException
     */
    public Message sendMessage(MsgBean msgBean, long timeOut) throws JMSException {
        if (null == msgBean) {
            return null;
        }
        long timeout = timeOut;
        if (timeout < 0) {
            timeout = 30000l;
        }
        Message result = null;
        Destination dest = session.createQueue(this.queueName);
        Destination replyTo = session.createTemporaryQueue();

        Message msg = session.createObjectMessage(msgBean);
        // msg.setJMSRedelivered(true); //对于重复发送的消息需要设置该消息头
        // msg.setJMSType("1"); //主要用于表示消息类型，以区分消息的结构
        // msg.setJMSExpiration(expiration);
        // msg.setJMSCorrelationID("correlationId:0000001");
        msg.setJMSReplyTo(replyTo);
        msg.setStringProperty("isNeedReply", "true");
        log.debug("begin to send: " + ((ObjectMessage) msg).getObject() + ", on[" + dest + "]");
        producer.send(dest, msg, Message.DEFAULT_DELIVERY_MODE, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

        // 返回消息监听
        MessageConsumer subConsumer = session.createConsumer(dest, null);
        result = subConsumer.receive(timeout);
        if (result == null) {
            log.debug("提示! JmsClient没有收到返回消息on Queue[" + replyTo + "],可能超时!!!");
        } else {
            log.debug("JmsClient收到返回信息：\r\n " + JmsMessageUtil.toXML(result));
        }

        // 释放资源
        subConsumer.close();
        if (replyTo instanceof TemporaryQueue) {
            ((TemporaryQueue) replyTo).delete();
        } else {
            ((TemporaryTopic) replyTo).delete();
        }

        return result;
    }

    /**
     * test     
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // JmsClient client = new JmsClient("tcp://172.16.29.151:6000",MsgConstant.ASIP_QUEUE_OPERATION);
        // int total = 1;
        // //while (total++ < 5) {
        // MsgBean vo = new MsgBean();
        // //vo.setMsgType(total);
        // vo.setMsgType(MsgConstant.MSG_TYPE_LOG4j_CLOSEDEBUG);
        // vo.setContext(MsgConstant.CONTEXT_LOCALHOST); //MsgConstant.CONTEXT_LOCALHOST
        // vo.setTimeOut(40000);
        // client.sendMessage(vo);
        // //Thread.sleep(1000);
        // // }
        // client.close();
        // }
        ConnectionFactory connectionFactory1 = new ActiveMQConnectionFactory("tcp://127.0.0.1:9003");
        Connection connection1 = connectionFactory1.createConnection();
        connection1.start();
        Session session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Destination destination = session.createQueue("myQueue");
        Destination destination1 = session1.createQueue("topic:asip.vm.insert.interface.log.channel");
        MessageProducer producer1 = session1.createProducer(destination1);
        producer1.setDeliveryMode(DeliveryMode.PERSISTENT);
        // while (true) {
        TextMessage message = session1.createTextMessage();
        message.setText("message_" + System.currentTimeMillis());
        producer1.send(message);
        System.out.println("Sent topic:asip.vm.insert.interface.log.channel(queue) message: " + message.getText());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // }

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:9003");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Destination destination = session.createQueue("myQueue");
        // MessageConsumer consumer = session.createConsumer(destination);
        Destination destination = session.createTopic("asip.vm.insert.interface.log.channel");
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
//                    TextMessage tm = (TextMessage) message;
                    System.out.println("asip.vm.insert.interface.log.channel(topic) Received message: " + message.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Destination destination2 = session.createQueue("topic:asip.vm.insert.interface.log.channel");
        MessageConsumer consumer2 = session.createConsumer(destination2);
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
                    TextMessage tm = (TextMessage) message;
                    System.out.println("topic:asip.vm.insert.interface.log.channel(topic) Received message: " + tm.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}