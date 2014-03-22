/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.monitor.exception.strategy;

import java.util.Map;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.client.LocalMuleClient;
import org.mule.exception.DefaultServiceExceptionStrategy;

import com.regaltec.asip.common.AsipLog4j;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-11-23 下午03:08:13</p>
 *
 * @author yihaijun
 */
public class AsipCxfComponentExceptionStrategy extends DefaultServiceExceptionStrategy {
    AsipLog4j logger = new AsipLog4j(this.getClass().getName());

    @SuppressWarnings("deprecation")
    public AsipCxfComponentExceptionStrategy(MuleContext muleContext) {
        setMuleContext(muleContext);
    }

    @Override
    public MuleEvent handleException(Exception exception, MuleEvent event) {
        MuleEvent result = super.handleException(exception, event);
        try {
            StringBuffer buf = new StringBuffer();
            buf.delete(0, buf.length());
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buf.append("\r\n<service>");
            buf.append("\r\n\t<head>");
            buf.append("\r\n\t\t<sender>IDA40_ASIP_MSG_HEAD_SENDER</sender>");
            buf.append("\r\n\t\t<receiver>IDA40_ASIP_MSG_HEAD_RECEIVER</receiver>");
            buf.append("\r\n\t\t<time>IDA40_ASIP_MSG_HEAD_TIME</time>");
            buf.append("\r\n\t\t<service_name>IDA40_ASIP_MSG_HEAD_SERVICE_NAME</service_name>");
            buf.append("\r\n\t\t<msg_type>response</msg_type>");
            buf.append("\r\n\t\t<msg_id>IDA40_ASIP_MSG_HEAD_MSG_ID</msg_id>");
            buf.append("\r\n\t\t<simulate_flag>IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG</simulate_flag>");
            buf.append("\r\n\t\t<error_code>ASIP-2034</error_code>");
            buf.append("\r\n\t\t<error_info>Asip WebService exception</error_info>");
            buf.append("\r\n\t</head>");
            buf.append("\r\n</service>");

            result.getMessage().setPayload(buf.toString());

            return result;
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                 e.printStackTrace();
            }
            return result;
        }
    }

    protected void defaultHandler(Throwable t) {
        logger.error(t.getMessage());
    }

}
