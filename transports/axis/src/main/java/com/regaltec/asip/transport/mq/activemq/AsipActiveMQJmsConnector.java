/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.transport.mq.activemq;

import java.lang.reflect.InvocationTargetException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.transport.ConnectException;
import org.mule.util.ClassUtils;

import com.regaltec.asip.common.AsipLog4j;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-3-27 下午01:46:26</p>
 *
 * @author yihaijun
 */
public class AsipActiveMQJmsConnector extends org.mule.transport.jms.activemq.ActiveMQJmsConnector {
    private AsipLog4j asipLog4j = new AsipLog4j(this.getClass().getName());

    private Connection asipJmsConnection = null;
    private Connection asipTmpJmsConnection = null;
    private ConnectionFactory vmConnectionFactory = null;

    public AsipActiveMQJmsConnector(MuleContext context) {
        super(context);
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("AsipActiveMQJmsConnector().");
        }
    }

    protected Connection createConnection() throws NamingException, JMSException, InitialisationException {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("createConnection.");
        }
        try {
            asipJmsConnection = super.createConnection();
        } catch (Exception e) {
            if (asipLog4j.isDebugEnabled()) {
                asipLog4j.debug("createConnection() Exception:" + e.toString());
            }
        }
        return asipJmsConnection;// getAsipActiveMQJmsConnection();
    }

    protected ConnectionFactory getDefaultConnectionFactory() throws Exception {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getDefaultConnectionFactory() begin.");
        }
        ConnectionFactory connectionFactory = (ConnectionFactory) ClassUtils.instanciateClass(
                "com.regaltec.asip.transport.mq.activemq.AsipActiveMQConnectionFactory", getBrokerURL());
        applyVendorSpecificConnectionFactoryProperties(connectionFactory);
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getDefaultConnectionFactory() return.");
        }
        return connectionFactory;
    }

    public Connection getConnection() {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getConnection begin...");
        }
        Connection theConnection = null;
        if (asipJmsConnection != null) {
            theConnection = asipJmsConnection;
        } else {
            try {
                theConnection = createConnection();
            } catch (Exception e) {
                // e.printStackTrace();
                asipLog4j.error("getConnection: createConnection exception:" + e.toString());
            }
        }
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getConnection() return.");
        }
        return theConnection;
    }

    private Connection getLocalTmpConnection() {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getLocalTmpConnection() begin.");
        }
        try {
            if (asipTmpJmsConnection == null) {
                // if (tmpConnectionFactory == null) {
                // tmpConnectionFactory = (ConnectionFactory) ClassUtils.instanciateClass(
                // "com.regaltec.asip.transport.mq.activemq.AsipActiveMQConnectionFactory",
                // "vm://localhost?broker.persistent=false&amp;broker.useJmx=false");
                // }
                // asipTmpJmsConnection = tmpConnectionFactory.createConnection();
                asipTmpJmsConnection = vmConnectionFactory.createConnection();
            }
        } catch (Exception e) {
            e.printStackTrace(); // 这个应该不会呀
            asipLog4j.debug("getLocalTmpConnection() Exception:" + e.toString());
        }
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getLocalTmpConnection() return.");
        }
        return asipTmpJmsConnection;
    }

    public Session getSession(boolean transacted, boolean topic) throws JMSException {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getSession begin.");
        }
        Session asipSession = null;
        if (asipJmsConnection == null || this.getConnection() == null) {
            if (asipLog4j.isDebugEnabled()) {
                asipLog4j.debug("getSession:getAsipActiveMQJmsConnection().");
            }
            getAsipActiveMQJmsConnection();
        }
        asipSession = super.getSession(transacted, topic);
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getSession return.");
        }
        return asipSession;
    }

    protected void doDisconnect() throws ConnectException {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("doDisconnect begin.");
        }
        try {
            super.doDisconnect();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (asipTmpJmsConnection != null) {
            try {
                asipTmpJmsConnection.close();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("doDisconnect return.");
        }
        asipJmsConnection = null;
        asipTmpJmsConnection = null;
        // tmpConnectionFactory = null;

    }

    /**
     * @return the vmConnectionFactory
     */
    public ConnectionFactory getVmConnectionFactory() {
        return vmConnectionFactory;
    }

    /**
     * @param vmConnectionFactory the vmConnectionFactory to set
     */
    public void setVmConnectionFactory(ConnectionFactory vmConnectionFactory) {
        this.vmConnectionFactory = vmConnectionFactory;
    }

    private Connection getAsipActiveMQJmsConnection() {
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getAsipActiveMQJmsConnection() begin...");
        }
        Connection theConnection = null;
        if (asipJmsConnection != null) {
            theConnection = asipJmsConnection;
        } else {
            try {
                asipJmsConnection = super.createConnection();
                theConnection = asipJmsConnection;
            } catch (Exception e) {
                asipLog4j.error("getAsipActiveMQJmsConnection:createConnection exception:" + e.toString());
                theConnection = getLocalTmpConnection();
            }
        }
        if (super.getConnection() != null && super.isConnected() && super.isStarted() && asipJmsConnection != null) {
            // try {
            // super.stop();
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            try {
                setConnection(theConnection);
                // super.start();
                // } catch (MuleException e) {
                // e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (asipLog4j.isDebugEnabled()) {
            asipLog4j.debug("getAsipActiveMQJmsConnection() return.");
        }
        return theConnection;

    }
}