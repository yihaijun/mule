/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.component;

import javax.activation.DataHandler;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.message.ds.StringDataSource;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.http.components.RestServiceWrapper;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-5-29 下午03:18:15</p>
 *
 * @author yihaijun
 */
public class AsipRestServiceWrapperComponent extends RestServiceWrapper implements Callable {
    public Object onCall(MuleEventContext context) throws Exception {
        setServiceUrl((String) context.getMessage().getInboundProperty(
                "IDA40_ASIP_MSG_HEAD_AsipRestServiceWrapper_serviceUrl"));
        EndpointURIEndpointBuilder endpointBuilder =
                new EndpointURIEndpointBuilder(getServiceUrl(), context.getMuleContext());
        MuleEvent event =
                new DefaultMuleEvent(context.getMessage(), endpointBuilder.buildOutboundEndpoint(),
                        context.getSession());
        return doInvoke(event);
    }

    @Override
    protected void doInitialise() throws InitialisationException {
        setServiceUrl("http://172.16.29.103:8082/portal/multigateway.p");
        setHttpMethod(HttpConstants.METHOD_POST);
        super.doInitialise();
    }

    @Override
    public Object doInvoke(MuleEvent event) throws Exception {
        setServiceUrl((String) event.getMessage().getInboundProperty(
                "IDA40_ASIP_MSG_HEAD_AsipRestServiceWrapper_serviceUrl"));
        if( event.getMessage().getInboundProperty("IDA40_ASIP_MSG_HEAD_AsipRestServiceWrapper_HttpMethod") != null ){
	        setHttpMethod((String) event.getMessage().getInboundProperty(
                "IDA40_ASIP_MSG_HEAD_AsipRestServiceWrapper_HttpMethod"));
        }
        java.util.ArrayList<String> tmpPayloadParameterNames = new java.util.ArrayList<String>();
        String keyNames = (String) event.getMessage().getInboundProperty("IDA40_ASIP_MSG_HEAD_AsipRestServiceWrapper_PayloadParameterNames");
        String[] keyList = keyNames.split(",");
        for (int i = 0; i < keyList.length; i++) {
            tmpPayloadParameterNames.add(keyList[i]);
        }
        setPayloadParameterNames(tmpPayloadParameterNames);
        MuleMessage  message = event.getMessage();
        String keyHandsNames = (String) event.getMessage().getInboundProperty("IDA40_ASIP_MSG_HEAD_AsipRestServiceWrapper_HandsParameterNames","");
        String[] keyHandsList = keyHandsNames.split(",");
        for (int i = 0; i < keyHandsList.length; i++) {
            message.setOutboundProperty(keyHandsList[i], message.getInboundProperty(keyHandsList[i]));
        }
        if(message.getInboundAttachmentNames()!=null){
            java.util.Iterator<String> it = message.getInboundAttachmentNames().iterator();
            while(it.hasNext()){
                String keyName = it.next();
                if(keyName.equals("payload")){
                    continue;
                }
                message.addOutboundAttachment(keyName, message.getInboundAttachment(keyName));
            }
            message.setOutboundProperty("Content-Type","multipart/form-data; boundary="+com.regaltec.asip.transformer.AppMessageToHttpRequest.MULTIPART_BOUNDARY);
            for (int i = 0; i < keyList.length; i++) {
                StringDataSource stringDataSource  = new StringDataSource(((Object [])message.getPayload())[i].toString(),keyList[i]);
                DataHandler dataHandler = new DataHandler(stringDataSource);
                message.addOutboundAttachment(keyList[i],dataHandler);
            }
        }
        return super.doInvoke(event);
    }
}
