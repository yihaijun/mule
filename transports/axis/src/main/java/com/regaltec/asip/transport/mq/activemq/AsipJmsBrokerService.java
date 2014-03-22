/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.transport.mq.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

import com.regaltec.asip.common.AsipLog4j;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-3-30 上午01:22:55</p>
 *
 * @author yihaijun
 */
public class AsipJmsBrokerService {
    private String brokerURL;
    private static BrokerService broker = null;
    private TransportConnector transportConnector = null;
    private AsipLog4j logger = new AsipLog4j(this.getClass().getName());

    /**
     * @return the brokerURL
     */
    public synchronized String getBrokerURL() {
        return brokerURL;
    }

    /**
     * @param brokerURL the brokerURL to set
     */
    public synchronized void setBrokerURL(String brokerURL) {
        logger.info("setBrokerURL(" + brokerURL + ") begin...");
        this.brokerURL = brokerURL;
        try {
            if(broker==null){
                broker = new BrokerService();
            }
            if (broker != null && broker.isStarted()) {
                logger.info("setBrokerURL:broker != null && broker.isStarted().");
                broker.stop();
            }
            if (transportConnector != null) {
                logger.info("setBrokerURL:transportConnector != null.");
                try {
                    broker.removeConnector(transportConnector);
                    transportConnector.stop();
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
            logger.info("setBrokerURL:addConnector() begin...");
            transportConnector = broker.addConnector(brokerURL);
            if (!broker.isStarted()) {
                logger.info("setBrokerURL:start() begin...");
                broker.start();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        logger.info("setBrokerURL(" + brokerURL + ") return.");
    }

    public AsipJmsBrokerService() {

    }

    public void close() {
        logger.info("close() begin.");
        if (transportConnector != null) {
            try {
                transportConnector.stop();
                broker.removeConnector(transportConnector);
                transportConnector = null;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        if (broker != null && broker.isStarted()) {
            try {
                logger.info("close():stop begin.");
                broker.stop();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        logger.info("close() return.");
    }
}
