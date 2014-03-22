/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.transformer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-9-27 下午06:41:35</p>
 *
 * @author yihaijun
 */
public class PrintMessageAtTransformer extends AbstractMessageTransformer{

    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        StringBuffer  buf = new StringBuffer();
        String  names = "";
        Object  []nameArry = null;
        int i = 0;

        Object payload = message.getPayload();
        buf.append("\r\ngetInvocationPropertyNames()=[");
        nameArry = message.getInvocationPropertyNames().toArray();
        names=nameArry.toString();
        buf.append(names);
        buf.append("]");
        i=0;
        while (nameArry!=null && i < nameArry.length) {
                buf.append("\r\n\tgetInvocationProperty(");
                buf.append(nameArry[i]);
                buf.append(")=[");
                buf.append(message.getInvocationProperty(nameArry[i].toString()));
                buf.append("]");
                i++;
        }

        buf.append("\r\ngetInboundPropertyNames()=[");
        nameArry = (message.getInboundPropertyNames()).toArray();
        names=nameArry.toString();
        buf.append(names);
        buf.append("]");
        i = 0;
        while (nameArry!=null && i < nameArry.length) {
                buf.append("\r\n\tgetInboundProperty(");
                buf.append(nameArry[i]);
                buf.append(")=[");
                buf.append(message.getInboundProperty(nameArry[i].toString()));
                buf.append("]");
                i++;
        }

        buf.append("\r\ngetOutboundPropertyNames()=[");
        nameArry =  (message.getOutboundPropertyNames()).toArray();
        names=nameArry.toString();
        buf.append(names);
        buf.append("]");
        nameArry =  (message.getOutboundPropertyNames()).toArray();
        i=0;
        while (nameArry!=null && i < nameArry.length) {
                buf.append("\r\n\tgetOutboundProperty(");
                buf.append(nameArry[i]);
                buf.append(")=[");
                buf.append(message.getOutboundProperty(nameArry[i].toString()));
                buf.append("]");
                i++;
        }

        buf.append("\r\ngetSessionPropertyNames()=[");
        nameArry =  (message.getSessionPropertyNames()).toArray();
        names=nameArry.toString();
        buf.append(names);
        buf.append("]");
        i=0;
        while (nameArry!=null && i < nameArry.length) {
                buf.append("\r\n\tgetSessionProperty(");
                buf.append(nameArry[i]);
                buf.append(")=[");
                buf.append(message.getSessionProperty(nameArry[i].toString()));
                buf.append("]");
                i++;
        }

        buf.append("\r\npayload.getClass()=[");
        buf.append(payload.getClass());
        buf.append("]\r\npayload=[");
        buf.append(payload.toString());
        buf.append("]");

        org.mule.api.ExceptionPayload exceptionPayload = message.getExceptionPayload();
        if(exceptionPayload != null){
                buf.append("\r\nExceptionPayload.getCode=");
                buf.append("" +exceptionPayload.getCode());
        }
        logger.info(buf.toString());

        return payload;
    }

}