/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.client.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQConnection;
import javax.jms.*;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-3-27 下午09:31:25</p>
 *
 * @author yihaijun
 */
public class ConsumerTool extends Thread implements MessageListener, ExceptionListener {
    private Object mutex = new Object();
    private int remaining = 0;
    /**
    * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
    * @param args
    */
    public static void main(String[] args) {
    }

    public void onMessage(Message message) {
        synchronized (mutex) {
            if (remaining == 0) {
                mutex.notify();// 唤醒线程
            }
        }
        // if (checkText(message, "SHUTDOWN")) {
        // try {
        // connection.close();
        // } catch (Exception e) {
        // e.printStackTrace(System.out);
        // }
        // } else if (checkText(message, "REPORT")) {
        // // send a report:
        // try {
        // long time = System.currentTimeMillis() - start;
        // String msg = "Received " + count + " in " + time + "ms";
        // producer.send(session.createTextMessage(msg));
        // } catch (Exception e) {
        // e.printStackTrace(System.out);
        // }
        // count = 0;
        //                 
        // } else {
        //                
        // if (count == 0) {
        // start = System.currentTimeMillis();
        // }
        //                 
        // if (++count % 1000 == 0) {
        // System.out.println("Received " + count + " messages.");
        // }
        // }
        // }
    }

    public void onException(JMSException exception) {
    }

    private void waitForCompletion() throws Exception {
        synchronized (mutex) {
            while (remaining > 0) {
                mutex.wait();// 赌赛线程
            }
        }
    }
}
