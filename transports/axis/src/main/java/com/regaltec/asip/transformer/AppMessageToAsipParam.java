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
public class AppMessageToAsipParam extends AbstractJmsTransformer {
    private String communicationProtocol = "jms";

    public AppMessageToAsipParam() {
        super();
    }

    @Override
    protected void declareInputOutputClasses() {
        registerSourceType(DataTypeFactory.create(Message.class));
        registerSourceType(DataTypeFactory.create(TextMessage.class));
        registerSourceType(DataTypeFactory.create(ObjectMessage.class));
        registerSourceType(DataTypeFactory.create(BytesMessage.class));
        registerSourceType(DataTypeFactory.create(MapMessage.class));
        registerSourceType(DataTypeFactory.create(StreamMessage.class));
        registerSourceType(DataTypeFactory.create(StreamMessage.class));
        registerSourceType(DataTypeFactory.create(AsipServiceParam.class));
    }

    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        if (communicationProtocol.equals("jms")) {
            return jmsToAsipServiceParam(message, outputEncoding);
        }
        Object payload = message.getPayload();
        if ((payload instanceof NullPayload) || (payload.equals("{NullPayload}"))) {
//            try {
//                throw new ResponseTimeoutException((org.mule.config.i18n.Message) message, RequestContext.getEvent(),
//                        RequestContext.getEvent().getEndpoint().getResponseMessageProcessors().get(0));
//            } catch (ResponseTimeoutException e) {
//                e.printStackTrace();
//            }
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

    public Object jmsToAsipServiceParam(MuleMessage message, String outputEncoding) throws TransformerException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Source object is " + ClassUtils.getSimpleName(message.getPayload().getClass()));
            }

            Object result = transformFromMessage((Message) message.getPayload(), outputEncoding);

            // We need to handle String / byte[] explicitly since this transformer does not define
            // a single return type
            if (returnType.getType().equals(byte[].class) && result instanceof String) {
                result = result.toString().getBytes(outputEncoding);
            } else if (returnType.getType().equals(String.class) && result instanceof byte[]) {
                result = new String((byte[]) result, outputEncoding);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Resulting object is " + ClassUtils.getSimpleName(result.getClass()));
            }

            return result;
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
    }
}
