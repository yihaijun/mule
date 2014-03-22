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
import java.util.Set;

import org.mule.RequestContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.service.Service;
import org.mule.api.service.ServiceAware;
import org.mule.service.ServiceCompositeMessageSource;

import com.regaltec.asip.common.AsipLog4j;
import com.regaltec.asip.utils.IdaMsgUtils;

/**
 * <p>asip代理综调向外发布的服务</p>
 * <p>创建日期：2010-9-19 下午06:24:15</p>
 *
 * @author yihaijun
 */
public class AsigServiceImpl implements ServiceAware, Serializable, AsigService {
    /**
     * 
     */
    private static final long serialVersionUID = -828817731486230620L;
    private MuleContext muleContext = null;

    /**
     * 
     */
    private AsipLog4j logger = new AsipLog4j(this.getClass().getName());

    /**
      * <p>asip代理综调向外发布的服务</p>
      * @param inputXml 服务请求参数组
      * @return xml字符串，它和inputXml 格式相同
      */
    public String call(String inputXml) {
        return callimpl(inputXml);
    }

    /**
      * <p>asip代理综调向外发布的服务</p>
      * @param inputXml 服务请求参数组
      * @return xml字符串，它和inputXml 格式相同
      */

    public String executeXML(String inputXml) {
        return callimpl(inputXml);
    }

    /**
      * <p>asip代理综调向旧接口平台发布的服务 </p>
      * @param inputXml 服务请求参数组
      * @return xml字符串，它和inputXml 格式相同
      */

    public String faultSend(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>集团派单</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocDispatchBill(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>变更接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocUpdateInfo(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>供集团系统调用的挂起接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocHungup(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>供集团系统调用的解挂接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocHungupRelieve(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>供集团系统调用的结单接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocBillFinish(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>供集团系统调用的交接接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocHandOver(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>供集团系统调用的退单接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocBillReject(String inputXml) {
        return callimpl(inputXml);
    }

    /**
    *
    * <p>供集团系统调用的业务恢复接口</p>
    * @param inputXml 传入的xml串
    * @return 统一格式返回
    * @throws BOException 业务处理异常
    */

    public String blocBusinessResume(String inputXml) {
        return callimpl(inputXml);
    }

    private String callimpl(String inputXml) {
        long beginTime = System.currentTimeMillis();
        long endTime = beginTime;
        long endTimeParse = beginTime;
        long endTimeCall = beginTime;
        long endTimeWriteLog = beginTime;
        long timeConsumingAB = 0; 
        long timeConsumingBC = 0; 
        long timeConsumingCD = 0; 

        
        // if (logger.isDebugEnabled()) {
        IdaMsgUtils.addAsigCallBegin();
        // }

        int call_timeout = 180000;
        int write_log_timeout = 3000;
        com.regaltec.asip.utils.PropertiesMapping conf = new com.regaltec.asip.utils.PropertiesMapping("conf/custom-mule-start.properties");
        call_timeout = Integer.parseInt(conf.getProperty("ida.webservice.asip.call.timeout")) + 1500;//加1500是因为这里比asig.vm.router.channel内部用时还要多一点
        write_log_timeout = Integer.parseInt(conf.getProperty("asip.in.one-way.max.timeout"))+ 500;

        try {
            AsigServiceParam asigServiceParam = new AsigServiceParam();
            asigServiceParam.parserBusinessServiceParam(inputXml);

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

            String result = "";

            Map<String, Object> properties = asigServiceParam.createMuleMessageProperties();

            endTimeParse = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("asigServiceParam.getParamNode().getFirstChild().getNodeName()="
                        + asigServiceParam.getParamNode().getFirstChild().getNodeName());
            }

            String appName = asigServiceParam.getAppName();
            MuleMessage message = null;
            try {
                LocalMuleClient localMuleClient = null; // 用于调用VM
                localMuleClient = RequestContext.getEventContext().getMuleContext().getClient();
                
                timeConsumingAB =  System.currentTimeMillis() - beginTime;
                properties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_AB", ""+timeConsumingAB );
                
                message = localMuleClient.send("vm://asig.vm.router.channel", asigServiceParam, properties,
                        call_timeout);
                endTimeCall = System.currentTimeMillis();
                
                setPropertySessionToOutbound(message, properties);

                timeConsumingBC =  System.currentTimeMillis() - beginTime - timeConsumingAB;
                properties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_BC", ""+timeConsumingBC);
                
                appName = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_APP_NAME", "UNKNOW");

                properties.put("IDA40_ASIP_MSG_HEAD_MSG_TYPE", "response");
                properties.put("IDA40_ASIP_MSG_HEAD_TIME", com.regaltec.asip.utils.DateUtils.now());
                
                timeConsumingCD =  System.currentTimeMillis() - beginTime - timeConsumingAB - timeConsumingBC;
                properties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_CD", ""+timeConsumingCD);

                try{
                localMuleClient.send("vm://asip.vm.insert.interface.log.proxy.channel", message.getPayload(),
                        properties, write_log_timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }                
                result = message.getPayload().toString();
                if (result.equals("{NullPayload}") || result.equals("NullPayload")) {
                    result = com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2033", "error return:"+result,timeConsumingAB,timeConsumingBC,timeConsumingCD);
                }
            } catch (Error e) {
                result = com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2032", e.toString(),timeConsumingAB,timeConsumingBC,timeConsumingCD);
            } catch (Exception e) {
                // e.printStackTrace();
                result = com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2031", e.toString(),timeConsumingAB,timeConsumingBC,timeConsumingCD);
            }
            endTimeWriteLog = System.currentTimeMillis();
            endTime = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                StringBuffer debugReport = new StringBuffer();
                debugReport.append("AsigServiceImpl." + properties.get("cxf_operation") + "(" + appName + ","
                        + asigServiceParam.getServiceName() + "," + asigServiceParam.getMsgId() + ") time-consuming:");
                debugReport.append((endTime - beginTime) + "ms.");
                debugReport.append("(" + (endTimeParse - beginTime));
                debugReport.append("+" + (endTimeCall - endTimeParse));
                debugReport.append("+" + (endTimeWriteLog - endTimeCall));
                debugReport.append(")");
                logger.debug(debugReport.toString());
            }
            return result;
        } finally {
            // if (logger.isDebugEnabled()) {
            IdaMsgUtils.addAsigCallReturn();
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
}
