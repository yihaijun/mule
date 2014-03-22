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
import java.util.Set;
import java.util.Iterator;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-9-27 下午07:07:52</p>
 *
 * @author yihaijun
 */
public class SetPropertySessionToOutbound extends AbstractMessageTransformer{

    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        Set<String> set = message.getSessionPropertyNames();
        Iterator<String> it = set.iterator();
        String key;
        Object value = "";
        while (it.hasNext()) {
            key = it.next();
            value = message.getSessionProperty(key);
            message.setOutboundProperty(key, value);
        }
        return message.getPayload();

    }
}
