/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.service;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mule.RequestContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.service.Service;
import org.mule.api.service.ServiceAware;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.service.ServiceCompositeMessageSource;

import com.regaltec.asip.common.AsipLog4j;
import com.regaltec.asip.utils.IdaMsgUtils;
import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>asip向综调发布的服务</p>
 * <p>创建日期：2010-9-19 下午06:24:15</p>
 *
 * @author yihaijun
 */
public class AsipServiceImpl implements ServiceAware, Serializable, AsipService {
    /**
     * 
     */
    private static final long serialVersionUID = 3422936495718086045L;
    private AsipLog4j logger = new AsipLog4j(this.getClass().getName());
    private MuleContext muleContext; // 暂时不用

    /**
         * <p>asip向综调发布的服务</p>
         * @param businessServiceName 指定业务服务名称
         * @param businessServiceParam 服务请求参数组
         * @param requestContextName 一般填空,但在需使用同一个类实例多个函数配合时就要使用
         * @return xml字符串，它和businessServiceParam 格式相同
         */
    public String call(String businessServiceName, String businessServiceParam, String requestContextName) {
        try {
            long beginTime = System.currentTimeMillis();
            long endTime = beginTime;
            long endTimeParse = beginTime;
            long endTimeCall = beginTime;
            long endTimeReturn = beginTime;
            long endTimeWriteLog = beginTime;
            long timeConsumingAB = 0L;
            long timeConsumingBC = 0L;
            long timeConsumingCD = 0L;

            if(businessServiceName.equalsIgnoreCase("sfiptestcall1")){
                return businessServiceParam;
            }
            // if (logger.isDebugEnabled()) {
            IdaMsgUtils.addAsipCallBegin();
            // }

            int call_timeout = 180000;
            int write_log_timeout = 3000;
            com.regaltec.asip.utils.PropertiesMapping conf =
                    new com.regaltec.asip.utils.PropertiesMapping("conf/custom-mule-start.properties");
            call_timeout = Integer.parseInt(conf.getProperty("asip.call.max.timeout")) + 1500;// 加1500是因为这里比asip.vm.asip.service.router.manager.channel内部用时还要多一点
            write_log_timeout = Integer.parseInt(conf.getProperty("asip.in.one-way.max.timeout")) + 500;

            if (businessServiceName.equals("DEBUGAsipService")) {
                return "AsipDebugReturnService response:[" + businessServiceParam + "]";
            }

            AsipServiceParam asipServiceParam = new AsipServiceParam();
            asipServiceParam.parserBusinessServiceParam(businessServiceParam);

            asipServiceParam.setAppName(businessServiceName);

            String result = "";
            Object payload = result;

            // Object[] param = { businessServiceName, businessServiceParam, requestContextName, asipServiceParam };
            Object[] param = { businessServiceName, requestContextName, asipServiceParam };
            Map<String, Object> properties = asipServiceParam.createMuleMessageProperties();

            endTimeParse = System.currentTimeMillis();

            if(businessServiceName.equalsIgnoreCase("sfiptestcall2")){
                return businessServiceParam;
            }
            MuleMessage message = null;
            try {
                LocalMuleClient localMuleClient = null; // 用于调用VM
                localMuleClient = RequestContext.getEventContext().getMuleContext().getClient();
                // MuleClient localMuleClient = new MuleClient(RequestContext.getEventContext().getMuleContext());

                if(businessServiceName.equalsIgnoreCase("sfiptestcall3")){
                    return businessServiceParam;
                }
                timeConsumingAB = System.currentTimeMillis() - beginTime;
                properties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_AB", "" + timeConsumingAB);

                Properties asip_properties = new PropertiesMapping("asipconf/asip.properties").getProperties();
                boolean asynchronous = true;
                boolean beforehandWarning = false;
                if (asip_properties != null) {
                    if (asip_properties.getProperty("interimNotSynchronizedCall.asynchronous", "true")
                            .equalsIgnoreCase("false")) {
                        asynchronous = false;
                    }
                    if (asip_properties.getProperty("interimNotSynchronizedCall.beforehandWarning", "true")
                            .equalsIgnoreCase("true")) {
                        beforehandWarning = true;
                    }
                }
                if(businessServiceName.equalsIgnoreCase("sfiptestcall4")){
                    return businessServiceParam;
                }
                if (beforehandWarning) {
                    boolean canCall =
                            new BeforehandWarningAsipServiceInterimNotSynchronizedCall().isCanCall(asipServiceParam
                                    .getServiceName());
                    if (!canCall) {
                        String preWarnTxt =
                                new BeforehandWarningAsipServiceInterimNotSynchronizedCall()
                                        .getPreWarnTxt(asipServiceParam.getServiceName());
                        if (preWarnTxt != null) {
                            Object[] warnParam = { "asip_beforehandWarning", "", preWarnTxt };
                            localMuleClient.send("vm://asip.vm.asip.service.router.manager.asynchronously.channel",
                                    warnParam, properties, call_timeout);
                        }
                    }
                }

                if(businessServiceName.equalsIgnoreCase("sfiptestcall5")){
                    return businessServiceParam;
                }
                boolean canCall =
                        new AsipServiceInterimNotSynchronizedCall().isCanCall(asipServiceParam.getServiceName());

                if(businessServiceName.equalsIgnoreCase("sfiptestcall6")){
                    return businessServiceParam;
                }
                if (canCall) {
                    message =
                            localMuleClient.send("vm://asip.vm.asip.service.router.manager.channel", param, properties,
                                    call_timeout);
                    if (businessServiceName.equals("DEBUGAsipVmAsipService")) {
                        return message.getPayload().toString();
                    }
                    payload = message.getPayload();
                } else {
                    if (asynchronous) {
                        localMuleClient.send("vm://asip.vm.asip.service.router.manager.asynchronously.channel", param,
                                properties, call_timeout);
                    }
                    String serviceName = asipServiceParam.getServiceName();
                    String asipName = "ASIP";
                    Properties t_asip_service_element_list =
                            new PropertiesMapping("asipconf/t_asip_service_element_list.properties").getProperties();
                    if (t_asip_service_element_list != null && t_asip_service_element_list.size() > 0) {
                        serviceName =
                                t_asip_service_element_list
                                        .getProperty(serviceName + "_asip_service_name", serviceName);
                        asipName = t_asip_service_element_list.getProperty("asip_service_name", asipName);
                    }
                    result =
                            com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2036", serviceName + "异常,"
                                    + asipName + "放弃调用此系统服务", timeConsumingAB, timeConsumingBC, timeConsumingCD);
                    payload = result;
                 }
                if(businessServiceName.equalsIgnoreCase("sfiptestcall7")){
                    return payload.toString();
                }

                endTimeCall = System.currentTimeMillis();
                if (canCall) {
                    setPropertySessionToOutbound(message, properties);
                }
                timeConsumingBC = System.currentTimeMillis() - beginTime - timeConsumingAB;
                properties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_BC", "" + timeConsumingBC);

                message =
                        localMuleClient.send("vm://asip.vm.asip.service.return.revise.channel", payload, properties,
                                write_log_timeout);
                endTimeReturn = System.currentTimeMillis();
                setPropertySessionToOutbound(message, properties);

                timeConsumingCD = System.currentTimeMillis() - beginTime - timeConsumingAB - timeConsumingBC;
                properties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_CD", "" + timeConsumingCD);

                if(businessServiceName.equalsIgnoreCase("sfiptestcall8")){
                    return payload.toString();
                }
                try {
                    // message =
                    localMuleClient.send("vm://asip.vm.insert.interface.log.proxy.channel", message.getPayload(),
                            properties, write_log_timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(businessServiceName.equalsIgnoreCase("sfiptestcall9")){
                    return payload.toString();
                }
                result = message.getPayload().toString();
                result =
                        org.apache.commons.lang.StringUtils.replace(result, "<time_consuming_cd>0</time_consuming_cd>",
                                "<time_consuming_cd>" + timeConsumingCD + "</time_consuming_cd>");
            } catch (Error e) {
                result =
                        com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2032", e.toString(),
                                timeConsumingAB, timeConsumingBC, timeConsumingCD);
                logger.error(e.toString());
            } catch (Exception e) {
                // e.printStackTrace();
                result =
                        com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2031", e.toString(),
                                timeConsumingAB, timeConsumingBC, timeConsumingCD);
                logger.error(e.toString());
            }
            if(businessServiceName.equalsIgnoreCase("sfiptestcall10")){
                return payload.toString();
            }
            endTimeWriteLog = System.currentTimeMillis();
            endTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                StringBuffer debugReport = new StringBuffer();
                debugReport.append("call(" + businessServiceName + "," + asipServiceParam.getServiceName() + ","
                        + asipServiceParam.getMsgId() + ") time-consuming:");
                debugReport.append((endTime - beginTime) + "ms.");
                debugReport.append("(" + (endTimeParse - beginTime));
                debugReport.append("+" + (endTimeCall - endTimeParse));
                debugReport.append("+" + (endTimeReturn - endTimeCall));
                debugReport.append("+" + (endTimeWriteLog - endTimeReturn));
                debugReport.append(")");
                debugReport.append(".asipCallBegin=" + IdaMsgUtils.getAsipCallBegin());
                debugReport.append(",asipCallReturn=" + IdaMsgUtils.getAsipCallReturn());
                logger.debug(debugReport.toString());

            }
            if(businessServiceName.equalsIgnoreCase("sfiptestcall11")){
                return payload.toString();
            }
            if (result.equals("{NullPayload}") || result.equals("NullPayload")) {
                return com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2033", "error return:" + result,
                        timeConsumingAB, timeConsumingBC, timeConsumingCD);
            }
            return result;
        } catch (Error e) {
        	e.printStackTrace();
            return com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2032", e.toString(), 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2031", e.toString(), 0, 0, 0);
        } finally {
            // if (logger.isDebugEnabled()) {
            IdaMsgUtils.addAsipCallReturn();
            // }
        }

    }

    private int setPropertySessionToOutbound(MuleMessage message, Map<String, Object> messageProperties) {
        Set<String> set = message.getSessionPropertyNames();
        Iterator<String> it = set.iterator();
        String key;
        Object value = "";
        while (it.hasNext()) {
            key = it.next();
            value = message.getSessionProperty(key);
            messageProperties.put(key, value);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public void setService(Service service) {
        long beginTime = System.currentTimeMillis();

        if (!(service.getMessageSource() instanceof ServiceCompositeMessageSource)) {
            throw new IllegalStateException("Only 'ServiceCompositeMessageSource' is supported");
        }

        muleContext = service.getMuleContext();
        // localMuleClient = muleContext.getClient();

        List endpoints = ((ServiceCompositeMessageSource) service.getMessageSource()).getEndpoints();
        if ((endpoints == null) || (endpoints.size() != 1)) {
            throw new IllegalArgumentException("AsipService is expected to have exactly 1 incoming endpoint.");
        }
        ImmutableEndpoint immutableEndpoint = (ImmutableEndpoint) endpoints.get(0);
        long endTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("setService() time-consuming:" + (endTime - beginTime) + "ms.");
            StringBuffer logEntry = new StringBuffer();
            logEntry.append("immutableEndpoint.getProperties()={");
            Map map = immutableEndpoint.getProperties();

            Iterator it = map.keySet().iterator();
            Object key;
            while (it.hasNext()) {
                key = it.next();
                logEntry.append(it.next());
                logEntry.append("=[");
                logEntry.append(map.get(key));
                logEntry.append("]\r\n");
            }
            logEntry.append("}");
            if (logger.isDebugEnabled()) {
                logger.debug(logEntry.toString());
            }
        }
    }

    /**
     * 
     * <p>暂时不用。</p>
     * @param uri
     * @return
     * @throws EndpointException
     * @throws InitialisationException
     */
    @SuppressWarnings("unused")
    private OutboundEndpoint createEndpoint(String uri) throws EndpointException, InitialisationException {
        EndpointURIEndpointBuilder endpointBuilder = new EndpointURIEndpointBuilder(uri, muleContext);

        OutboundEndpoint outboundEndpoint = endpointBuilder.buildOutboundEndpoint();
        return outboundEndpoint;
    }

}
