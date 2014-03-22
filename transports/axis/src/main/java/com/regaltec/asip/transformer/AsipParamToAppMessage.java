/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.transformer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.jms.transformers.AbstractJmsTransformer;
import org.mule.util.ClassUtils;

import com.regaltec.asip.service.AsipServiceParam;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-12-30 上午11:18:16</p>
 *
 * @author yihaijun
 */
public class AsipParamToAppMessage extends AbstractJmsTransformer{
    private String communicationProtocol ="jms";

    public AsipParamToAppMessage()
    {
        super();
    }
    @Override
    protected void declareInputOutputClasses()
    {
//        setReturnDataType(DataTypeFactory.create(Message.class));
        registerSourceType(DataTypeFactory.create(AsipServiceParam.class));
    }

    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException{
        if (logger.isDebugEnabled())
        {
            logger.debug("communicationProtocol:[" + communicationProtocol+"]");
        }
        if(communicationProtocol.equals("jms")){
            return toJmsMessage(message,  outputEncoding);
        }
        return message.getPayload();
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

    public Object toJmsMessage(MuleMessage message, String outputEncoding) throws TransformerException
    {
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Source object is " + ClassUtils.getSimpleName(message.getPayload().getClass()));
            }

            Object result = transformToMessage(message);

            if (logger.isDebugEnabled())
            {
                logger.debug("Resulting object is " + ClassUtils.getSimpleName(result.getClass()));
            }

            return result;
        }
        catch (Exception e)
        {
            throw new TransformerException(this, e);
        }
    }

}
