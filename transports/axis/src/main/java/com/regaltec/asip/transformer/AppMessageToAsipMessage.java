/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.transformer;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.mule.RequestContext;
import org.mule.api.MuleMessage;
import org.mule.api.routing.ResponseTimeoutException;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.CoreMessages;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.NullPayload;
import org.mule.transport.jms.transformers.AbstractJmsTransformer;
import org.mule.util.ClassUtils;

import com.regaltec.asip.service.AsipServiceParam;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-12-30 上午11:25:31</p>
 *
 * @author yihaijun
 */
public class AppMessageToAsipMessage extends AbstractMessageTransformer {
    private String communicationProtocol = "jms";

    public AppMessageToAsipMessage() {
        super();
    }

    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        // {
        // logger.error("1111111111111111111111111");
        // StringBuffer buf = new StringBuffer();
        // MuleMessage message = RequestContext.getEventContext().getMessage();
        // buf.append("\r\ngetInvocationPropertyNames()=[");
        // Set<String> nameArry = message.getInvocationPropertyNames();
        // String names = nameArry.toString();
        // buf.append(names);
        // buf.append("]");
        // int i = 0;
        // Iterator it = nameArry.iterator();
        // String keyName = "";
        // while (it.hasNext()) {
        // keyName = (String)it.next();
        // buf.append("\r\n\tgetInvocationProperty(");
        // buf.append(keyName);
        // buf.append(")=[");
        // buf.append(message.getInvocationProperty(keyName));
        // buf.append("]");
        // i++;
        // }
        // buf.append("\r\ngetInboundPropertyNames()=[");
        // nameArry = message.getInboundPropertyNames();
        // names = nameArry.toString();
        // buf.append(names);
        // buf.append("]");
        // i = 0;
        // it = nameArry.iterator();
        // keyName = "";
        // while (it.hasNext()) {
        // keyName = (String)it.next();
        // buf.append("\r\n\tgetInboundProperty(");
        // buf.append(keyName);
        // buf.append(")=[");
        // buf.append(message.getInboundProperty(keyName));
        // buf.append("]");
        // i++;
        // }
        // buf.append("\r\ngetOutboundPropertyNames()=[");
        // nameArry = message.getOutboundPropertyNames();
        // names = nameArry.toString();
        // buf.append(names);
        // buf.append("]");
        // i = 0;
        // it = nameArry.iterator();
        // keyName = "";
        // while (it.hasNext()) {
        // keyName = (String)it.next();
        // buf.append("\r\n\tgetOutboundProperty(");
        // buf.append(keyName);
        // buf.append(")=[");
        // buf.append(message.getOutboundProperty(keyName));
        // buf.append("]");
        // i++;
        // }
        // logger.error(buf.toString());
        // logger.error("22222222222222222");
        // }

        Object payload = message.getPayload();
        if (payload instanceof NullPayload || payload.equals("{NullPayload}")) {
            message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", "ASIP-2040");
            String error_info = "应用系统";
            error_info += message.getSessionProperty("IDA40_ASIP_MSG_HEAD_APP_NAME", "");
            error_info += "响应超时";
            message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", error_info);

            StringBuffer buf = new StringBuffer();
            buf.delete(0, buf.length());
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buf.append("\r\n<service>");
            buf.append("\r\n\t<head>");
            buf.append("\r\n\t\t<sender>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SENDER", ""));
            buf.append("</sender>");
            buf.append("\r\n\t\t<receiver>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_RECEIVER", ""));
            buf.append("</receiver>");
            buf.append("\r\n\t\t<time>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_TIME", ""));
            buf.append("</time>");
            buf.append("\r\n\t\t<service_name>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", ""));
            buf.append("</service_name>");
            buf.append("\r\n\t\t<msg_type>response</msg_type>");
            buf.append("\r\n\t\t<msg_id>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ID", ""));
            buf.append("</msg_id>");
            buf.append("\r\n\t\t<keyword>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_KEYWORD", ""));
            buf.append("</keyword>");
            buf.append("\r\n\t\t<simulate_flag>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG", ""));
            buf.append("</simulate_flag>");
            buf.append("\r\n\t\t<error_code>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", ""));
            buf.append("</error_code>");
            buf.append("\r\n\t\t<error_info>");
            buf.append(message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", ""));
            buf.append("</error_info>");
            buf.append("\r\n\t</head>");
            buf.append("\r\n\t<data_info>");
            buf.append("\r\n\t</data_info>");
            buf.append("\r\n</service>");
            return buf.toString();
        }
        if (message.getInboundProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE") != null) {
            message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", message
                    .getInboundProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE"));
        }
        if (message.getInboundProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO") != null) {
            message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", message.getInboundProperty(
                    "IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", ""));
        }
        if (message.getInboundProperty("session_IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE") != null) {
            message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", message
                    .getInboundProperty("session_IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE"));
        }
        if (message.getInboundProperty("session_IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO") != null) {
            message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", message.getInboundProperty(
                    "session_IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", ""));
        }
        return payload;
    }

    /**
     * @return the communicationProtocol
     */
    public String getCommunicationProtocol() {
        return communicationProtocol;
    }

    /**
     * @param communicationProtocol the communicationProtocol to set
     */
    public void setCommunicationProtocol(String communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

}
