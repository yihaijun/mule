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
 * <p>创建日期：2013-9-27 下午07:02:01</p>
 *
 * @author yihaijun
 */
public class WriteInterfaceLogToLog4j extends AbstractMessageTransformer{

    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        Object payload = message.getPayload();
        com.regaltec.asip.utils.PropertiesMapping conf = new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
        if (conf.getProperty("AsipLogFilter.filterAll").equals("true")){
                return payload;
        }
        if (conf.getProperty("AsipLogFilter.log4j.filterAll").equals("true")){
                return payload;
        }
             
        StringBuffer  buf = new StringBuffer();
        Object  []nameArry = null;
        int i = 0;

        i=0;
        nameArry = message.getSessionPropertyNames().toArray();
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
        buf.append("]\r\n");
        logger.info(buf.toString());
        return payload;
    }
}
