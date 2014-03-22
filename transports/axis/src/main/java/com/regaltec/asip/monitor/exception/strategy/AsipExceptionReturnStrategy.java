/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.monitor.exception.strategy;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.config.ExceptionHelper;
import org.mule.exception.AbstractMessagingExceptionStrategy;

import com.regaltec.asip.service.AsipServiceInterimNotSynchronizedCall;
import com.regaltec.asip.service.BeforehandWarningAsipServiceInterimNotSynchronizedCall;
import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>custom-exception-strategy,用于保证调用完整返回。</p>
 * <p>创建日期：2010-12-7 下午05:22:21</p>
 *
 * @author yihaijun
 */
public class AsipExceptionReturnStrategy extends AbstractMessagingExceptionStrategy {
    private int monitor_elem_id_length = 64;

    @Override
    public MuleEvent handleException(Exception exception, MuleEvent event) {
        String exceptionInMuleEndpoint = event.getMessage().getInboundProperty("MULE_ENDPOINT", "");
        String exceptionOutMuleEndpoint = event.getMessage().getOutboundProperty("MULE_ENDPOINT", "");
        if (logger.isDebugEnabled() || logger.isWarnEnabled()) {
            StringBuffer errlogBuf = new StringBuffer();
            errlogBuf.delete(0, errlogBuf.length());
            if (event != null) {
                writeMessageProperties(errlogBuf, event.getMessage(), exception.toString());
            }
            if (logger.isDebugEnabled()) {
                logger.debug(errlogBuf.toString());
            } else {
                logger.warn(errlogBuf.toString());
            }
        }
        if (exceptionOutMuleEndpoint.startsWith("jms://topic:")) {
            event.getMessage().setPayload("");
            return event;
        }
        String monitor_event_msg = exception.toString();
        MuleEvent result = super.handleException(exception, event);
        try {
            StringBuffer buf = new StringBuffer();
            buf.delete(0, buf.length());
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            buf.append("\r\n<service>");
            buf.append("\r\n\t<head>");
            buf.append("\r\n\t\t<sender>IDA40_ASIP_MSG_HEAD_SENDER</sender>");
            buf.append("\r\n\t\t<receiver>IDA40_ASIP_MSG_HEAD_RECEIVER</receiver>");
            buf.append("\r\n\t\t<time>");
            buf.append(com.regaltec.asip.utils.DateUtils.now());
            buf.append("</time>");
            buf.append("\r\n\t\t<service_name>IDA40_ASIP_MSG_HEAD_SERVICE_NAME</service_name>");
            buf.append("\r\n\t\t<msg_type>response</msg_type>");
            buf.append("\r\n\t\t<msg_id>IDA40_ASIP_MSG_HEAD_MSG_ID</msg_id>");
            buf.append("\r\n\t\t<simulate_flag>IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG</simulate_flag>");
            buf.append("\r\n\t\t<error_code>");
            /**
             *  ASIP-1030    接口服务未正常部署
                ASIP-2010       接口连接失败
                ASIP-2020       接口认证失败
                ASIP-2030       接口调用异常
                ASIP-2040       接口响应超时
                ASIP-2050       接口调用失败，如error_code为ASIP-2050，error_info为“请求失败，XXX系统返回错误，错误代码为XXX，错误描述为XXXXX”
                ASIP-3010       综调没有这样的服务（${serviceClass}）
                ASIP-3020       在综调服务（${serviceClass}）中没有功能（${serviceName}）
                ASIP-3050       综调服务调用失败，如error_code为ASIP-3050，error_info为“请求失败，${serviceClass}.${serviceName}服务返回错误，[错误代码为XXX，]错误描述为XXXXX”
                ASIP-4010       接口平台连接综调失败
                ASIP-4020       综调服务响应超时
                ASIP-4030       综调服务异常

             */
            String errorCode = "ASIP-2030";
            String errorInf = "服务异常";
            if (exception instanceof org.mule.api.routing.RoutingException) {
                if (exception.toString().indexOf("Failed to route event via endpoint") > -1) {
                    errorCode = "ASIP-2010";
                    errorInf = "接口连接失败(RoutingException)";
                }
                try {
                    if (exception.toString().indexOf("org.mule.api.transport.NoReceiverForEndpointException") > -1) {
                        errorCode = "ASIP-2035";
                        errorInf = "此服务不接受这么大并发请求(ERROR_IDA40_ASIP_MSG_HEAD_SERVICE_NAME)";
                    } else {
                        MuleException muleException = ExceptionHelper.getRootMuleException(exception);
                        logger.info(muleException.getDetailedMessage());
                        if (muleException.getDetailedMessage().indexOf(
                                "org.mule.api.transport.NoReceiverForEndpointException") > -1
                                || muleException.getDetailedMessage().indexOf(
                                        "There is no receiver registered on connector") > -1) {
                            errorCode = "ASIP-2035";
                            errorInf = "此服务不接受这么大并发请求(ERROR_IDA40_ASIP_MSG_HEAD_SERVICE_NAME)";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (exception instanceof org.mule.component.ComponentException) {
                String matching =
                        "Component that caused exception is: org.mule.component.DefaultJavaComponent component for: SedaService{";
                if (exception.toString().indexOf(matching) > -1) {
                    errorCode = "ASIP-2030(ComponentException)";
                }
            } else if (exception instanceof org.mule.api.transformer.TransformerMessagingException) {
                // 以下待编程实现和验证
                // if((src instanceof NullPayload) || (src.equals("{NullPayload}"))) {
                // message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE","ASIP-2040")
                // message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO","接口响应超时")
                // return
                // "<?xml version=\"1.0\" encoding=\"UTF-8\"?><service><head><error_code>ASIP-2040</error_code><error_info>接口响应超时</error_info></head></service>"
                // }
                String matching =
                        "org.mule.api.transformer.TransformerMessagingException: Failed to read payload data (javax.script.ScriptException). Message payload is of type: String";
                if (exception.toString().indexOf(matching) > -1) {
                    errorCode = "ASIP-2040";
                    errorInf = "服务响应超时(TransformerMessagingException)";
                } else {
                    errorInf = "消息转换异常(TransformerMessagingException)";
                }
            } else if (exception instanceof org.mule.api.routing.ResponseTimeoutException) {
                errorCode = "ASIP-2040";
                errorInf = "服务响应超时(ResponseTimeoutException)";
            } else if (exception instanceof org.mule.api.transport.NoReceiverForEndpointException) {
                errorCode = "ASIP-2035";
                errorInf = "此服务不接受这么大并发请求(ERROR_IDA40_ASIP_MSG_HEAD_SERVICE_NAME)";
            }

            buf.append(errorCode);
            buf.append("</error_code>");
            buf.append("\r\n\t\t<error_info>");
            try {
                if (exceptionInMuleEndpoint.equals("")) {
                    // errorInf = "UNKNOW MULE_ENDPOINT  Exception:";
                } else {
                    errorInf += "(" + exceptionInMuleEndpoint + ")";
                }
            } catch (Exception e) {
                // errorInf = "UNKNOW MULE_ENDPOINT  Exception:";
            }
            // errorInf = errorInf + exception.toString();
            buf.append(org.apache.commons.lang.StringEscapeUtils.escapeXml(errorInf));
            buf.append("\r\n\t\t</error_info>");
            buf.append("\r\n\t</head>");
            buf.append("\r\n</service>");

            result.getMessage().setPayload(buf.toString());
            // result.getMessage().setSessionProperty("ASIP_MONITOR_EVENT_EVENT_MSG", monitor_event_msg);
            // result.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", errorCode);
            // result.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", errorInf);
            addResultMuleMessageProperties(result, errorCode, errorInf, monitor_event_msg);

            LocalMuleClient localMuleClient = event.getMuleContext().getClient();

            String tmpServiceName = "";
            try {
                Map<String, Object> messageProperties = createMuleMessageProperties(event.getMessage());
                if (messageProperties.get("session_IDA40_ASIP_MSG_HEAD_SERVICE_NAME") != null) {
                    tmpServiceName = (String) messageProperties.get("session_IDA40_ASIP_MSG_HEAD_SERVICE_NAME");
                } else {
                    if (messageProperties.get("inbound_MULE_ENDPOINT") != null) {
                        Properties t_asip_service_element_list =
                                new PropertiesMapping("asipconf/t_asip_service_element_list.properties")
                                        .getProperties();
                        if (t_asip_service_element_list != null && t_asip_service_element_list.size() > 0) {
                            Iterator it = t_asip_service_element_list.keySet().iterator();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                String value = t_asip_service_element_list.getProperty(key);
                                if (value.equals(messageProperties.get("inbound_MULE_ENDPOINT"))) {
                                    int endPos = key.indexOf("_asip_service_code_");
                                    if (endPos > 0) {
                                        tmpServiceName = key.substring(0, endPos);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    messageProperties.put("session_IDA40_ASIP_MSG_HEAD_SERVICE_NAME", tmpServiceName);
                    result.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", tmpServiceName);
                }
                if (messageProperties.get("session_ASIP_MONITOR_EVENT_EVENT_MSG") == null) {
                    messageProperties.put("session_ASIP_MONITOR_EVENT_EVENT_MSG", "");
                    result.getMessage().setSessionProperty("ASIP_MONITOR_EVENT_EVENT_MSG", "");
                }
                if (messageProperties.get("inbound_MULE_ENDPOINT") == null) {
                    messageProperties.put("inbound_MULE_ENDPOINT", "ENDPOINT_error");
                }
                int write_log_timeout = 3000;
                com.regaltec.asip.utils.PropertiesMapping conf =
                        new com.regaltec.asip.utils.PropertiesMapping("conf/custom-mule-start.properties");
                write_log_timeout = Integer.parseInt(conf.getProperty("asip.in.one-way.max.timeout")) + 500;
                localMuleClient.send("vm://asip.vm.exception.based.router.proxy.channel", buf.toString(),
                        messageProperties, write_log_timeout);
                if (!exceptionInMuleEndpoint.equals("")) {
                    String channel = exceptionInMuleEndpoint;
                    if (channel.indexOf("://") > 0) {
                        channel = channel.substring(channel.indexOf("://") + 3);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("exception channel:" + channel);
                    } else if (logger.isWarnEnabled()) {
                        logger.warn("exception channel:" + channel);
                    }
                    new BeforehandWarningAsipServiceInterimNotSynchronizedCall().registerException(channel, errorCode,
                            errorInf);
                    new AsipServiceInterimNotSynchronizedCall().registerException(channel, errorCode, errorInf);
                }
                new AsipServiceInterimNotSynchronizedCall().registerException(
                        messageProperties.get("session_IDA40_ASIP_MSG_HEAD_SERVICE_NAME").toString(), errorCode,
                        errorInf);
                new BeforehandWarningAsipServiceInterimNotSynchronizedCall().registerException(
                        messageProperties.get("session_IDA40_ASIP_MSG_HEAD_SERVICE_NAME").toString(), errorCode,
                        errorInf);

                Properties asip_properties = new PropertiesMapping("asipconf/asip.properties").getProperties();
                boolean reportExceptionToIda = false;
                if (asip_properties != null) {
                    if (asip_properties.getProperty("interimNotSynchronizedCall.reportExceptionToIda", "false")
                            .equalsIgnoreCase("true")) {
                        reportExceptionToIda = true;
                    }
                }
                if (reportExceptionToIda) {
                    try {
                        Object[] warnParam = { "asip_Exception", "", buf.toString() };
                        for (final Object propertyName : result.getMessage().getSessionPropertyNames()) {
                            if (!(propertyName instanceof String)) {
                                continue;
                            }
                            String keyName = (String) propertyName;
                            if (messageProperties.get(keyName) == null) {
                                messageProperties.put(keyName,
                                        result.getMessage().getSessionProperty((String) propertyName));
                            }
                        }
                        java.util.ArrayList<String> addKeys = new java.util.ArrayList<String>();
                        java.util.Iterator it = messageProperties.keySet().iterator();
                        while (it.hasNext()) {
                            Object propertyName = it.next();
                            if (!(propertyName instanceof String)) {
                                continue;
                            }
                            String keyName = (String) propertyName;
                            if (!keyName.startsWith("session_")) {
                                continue;
                            }
                            keyName = ((String) propertyName).substring("session_".length());
                            if (messageProperties.get(keyName) == null) {
                                addKeys.add(keyName);
                            }
                        }
                        for (int i = 0; i < addKeys.size(); i++) {
                            messageProperties.put(addKeys.get(i),
                                    messageProperties.get("session_" + addKeys.get(i)));
                        }
                        if (((String)warnParam[2]).indexOf("<service_name>IDA40_ASIP_MSG_HEAD_SERVICE_NAME</service_name>") > 0) {
                            warnParam[2] = buf.toString().replace("<service_name>IDA40_ASIP_MSG_HEAD_SERVICE_NAME</service_name>", "<service_name>"+tmpServiceName+"</service_name>");
                        }
                        localMuleClient.send("vm://asip.vm.asip.service.router.manager.asynchronously.channel",
                                warnParam, messageProperties, write_log_timeout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (MuleException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
            if (buf.toString().indexOf("ERROR_IDA40_ASIP_MSG_HEAD_SERVICE_NAME") > 0) {
                result.getMessage().setPayload(
                        buf.toString().replace("ERROR_IDA40_ASIP_MSG_HEAD_SERVICE_NAME", tmpServiceName));
            }
            result.getMessage().setExceptionPayload(null);
            return result;
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            return result;
        }

    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createMuleMessageProperties(MuleMessage message) {
        Properties messageProperties = new Properties();
        Set<String> set = message.getSessionPropertyNames();
        Iterator<String> it = set.iterator();
        String key;
        Object value = "";
        while (it.hasNext()) {
            key = it.next();
            value = message.getSessionProperty(key);
            messageProperties.put("session_" + key, value);
        }
        set = message.getInboundPropertyNames();
        it = set.iterator();
        while (it.hasNext()) {
            key = it.next();
            value = message.getInboundProperty(key);
            messageProperties.put("inbound_" + key, value);
        }
        set = message.getOutboundPropertyNames();
        it = set.iterator();
        while (it.hasNext()) {
            key = it.next();
            value = message.getOutboundProperty(key);
            messageProperties.put("outbound_" + key, value);
        }
        if (messageProperties.get("inbound_MULE_ENDPOINT") == null) {
            String inbound_MULE_ENDPOINT =
                    message.getInboundProperty("MULE_ORIGINATING_ENDPOINT", "UNKNOW MULE_ENDPOINT");
            messageProperties.put("inbound_MULE_ENDPOINT", inbound_MULE_ENDPOINT);
        }
        String inbound_MULE_ENDPOINT = messageProperties.get("inbound_MULE_ENDPOINT").toString();
        if (inbound_MULE_ENDPOINT.length() > monitor_elem_id_length) {
            try {
                int begin = inbound_MULE_ENDPOINT.length() - ".channel".length() - monitor_elem_id_length;
                if (begin < 0) {
                    begin = 0;
                }
                int end = inbound_MULE_ENDPOINT.length() - ".channel".length();
                messageProperties.put("inbound_MULE_ENDPOINT", inbound_MULE_ENDPOINT.substring(begin, end));
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                messageProperties.put("inbound_MULE_ENDPOINT", "createMsgProperties Exception");
            }
        }
        return (Map) messageProperties;
    }

    @SuppressWarnings("unchecked")
    private int addResultMuleMessageProperties(MuleEvent result, String errorCode, String errorInf,
            String monitor_event_msg) {
        result.getMessage().setOutboundProperty("ASIP_MONITOR_EVENT_EVENT_MSG", monitor_event_msg);
        result.getMessage().setOutboundProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", errorCode);
        result.getMessage().setOutboundProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", errorInf);
        result.getMessage().setSessionProperty("ASIP_MONITOR_EVENT_EVENT_MSG", monitor_event_msg);
        result.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", errorCode);
        result.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", errorInf);

        MuleMessage message = result.getMessage();
        for (final Object propertyName : message.getSessionPropertyNames()) {
            if (!(propertyName instanceof String)) {
                continue;
            }
            message.setOutboundProperty("session_" + propertyName, message.getSessionProperty(propertyName.toString()));
        }

        for (final Object propertyName : message.getInboundPropertyNames()) {
            if (!(propertyName instanceof String)) {
                continue;
            }
            message.setOutboundProperty("inbound_" + propertyName, message.getInboundProperty(propertyName.toString()));
        }
        if (message.getOutboundProperty("inbound_MULE_ENDPOINT") == null) {
            message.setOutboundProperty("inbound_MULE_ENDPOINT", "UNKNOW MULE_ENDPOINT");
        }
        return 0;
    }

    private void writeMessageProperties(final StringBuffer logEntry, final MuleMessage message,
            final String exceptionToString) {

        logEntry.append("sessionPropertyNames={\r\n");
        for (final Object sessionPropertyName : message.getSessionPropertyNames()) {
            logEntry.append("  ");
            logEntry.append(sessionPropertyName);
            logEntry.append("=");
            logEntry.append(message.getSessionProperty(sessionPropertyName.toString()));
            logEntry.append("\r\n");
        }
        logEntry.append("}\r\n");

        logEntry.append("invocationProperty={\r\n");
        for (final Object invocationPropertyName : message.getInvocationPropertyNames()) {
            logEntry.append("  ");
            logEntry.append(invocationPropertyName);
            logEntry.append("=");
            logEntry.append(message.getInvocationProperty(invocationPropertyName.toString()));
            logEntry.append("\r\n");
        }
        logEntry.append("}\r\n");

        logEntry.append("inboundProperty={\r\n");
        for (final Object inboundPropertyName : message.getInboundPropertyNames()) {
            logEntry.append("  ");
            logEntry.append(inboundPropertyName);
            logEntry.append("=");
            logEntry.append(message.getInboundProperty(inboundPropertyName.toString()));
            logEntry.append("\r\n");
        }
        logEntry.append("}\r\n");

        logEntry.append("outboundProperty={\r\n");
        for (final Object outboundPropertyName : message.getOutboundPropertyNames()) {
            logEntry.append("  ");
            logEntry.append(outboundPropertyName);
            logEntry.append("=");
            logEntry.append(message.getOutboundProperty(outboundPropertyName.toString()));
            logEntry.append("\r\n");
        }
        logEntry.append("}\r\n");
        logEntry.append("exceptionToString=[");
        logEntry.append(exceptionToString);
        logEntry.append("]\r\n");
    }
}
