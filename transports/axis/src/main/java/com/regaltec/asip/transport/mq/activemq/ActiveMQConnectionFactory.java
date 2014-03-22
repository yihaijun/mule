/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.transport.mq.activemq;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-3-4 上午09:13:56</p>
 *
 * @author yihaijun
 */
public class ActiveMQConnectionFactory extends org.apache.activemq.ActiveMQConnectionFactory {
    /**
     * @return Returns the Connection.
     */
    @Override
    public Connection createConnection() throws JMSException {
        try {
            return createActiveMQConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return Returns the Connection.
     */
    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        try {
            return createActiveMQConnection(userName, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        try {
            return super.createQueueConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        try {
            return super.createQueueConnection(userName, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        try {
            return super.createTopicConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        try {
            return super.createTopicConnection(userName, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
