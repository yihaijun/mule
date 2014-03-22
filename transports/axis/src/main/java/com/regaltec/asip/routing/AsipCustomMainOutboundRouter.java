/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.routing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jdom.Element;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.RequestContext;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.routing.RoutePathNotFoundException;
import org.mule.api.routing.RoutingException;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.Connector;
import org.mule.config.i18n.CoreMessages;
import org.mule.endpoint.DefaultOutboundEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.routing.outbound.AbstractOutboundRouter;
import org.mule.util.IOUtils;

import com.regaltec.asip.routing.filter.AsipLogFilter;
import com.regaltec.asip.utils.IdaMsgUtils;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-4-9 下午03:58:40</p>
 * @author yihaijun
 * Edit by yihaijun at 2013-06-26:
 * NMA-149 修复服务配置响应时间,调用方式不生效问题,路由配置改成根据文件时间判断是否要重新加载,返回值将不再判断一下个路由
 */
public class AsipCustomMainOutboundRouter extends AbstractOutboundRouter {
    private static Map<String, AsipDynamicRouteProcessor> dynamicRouteProcessor_CACHE =
            new HashMap<String, AsipDynamicRouteProcessor>();

    public boolean isMatch(MuleMessage message) throws MuleException {
        return true;
    }

    @Override
    protected MuleEvent route(MuleEvent event) throws MessagingException {
        MuleEvent result = null;
        StringBuffer msgLogBuf = new StringBuffer();

        int call_timeout = 180000;
        int write_log_timeout = 3000;
        com.regaltec.asip.utils.PropertiesMapping conf =
                new com.regaltec.asip.utils.PropertiesMapping("conf/custom-mule-start.properties");
        call_timeout = Integer.parseInt(conf.getProperty("ida.webservice.asip.call.timeout")) + 1500;// 加1500是因为这里比asig.vm.router.channel内部用时还要多一点
        write_log_timeout = Integer.parseInt(conf.getProperty("asip.in.one-way.max.timeout")) + 500;

        if (logger.isDebugEnabled() || logger.isWarnEnabled()) {
            if (logger.isDebugEnabled()) {
                msgLogBuf.delete(0, msgLogBuf.length());
                if (event != null) {
                    writeMessageProperties(msgLogBuf, event.getMessage());
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(msgLogBuf.toString());
                }
            }
        }
        if (routes == null || routes.size() == 0) {
            throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), event, null);
        }

        String routerServiceFileName =
                event.getMessage().getOutboundProperty("IDA40_ASIP_MSG_HEAD_ROUTERSERVICE_CONF",
                        "/asipconf/kangaroo/KangarooMainRouterService.xml");
        String muleHome = com.regaltec.asip.manager.api.util.SystemUtil.getEnvValueByName("MULE_HOME");

        if (!muleHome.endsWith("\\") && !muleHome.endsWith("/")) {
            muleHome = muleHome + "/";
        }
        if (routerServiceFileName.startsWith("\\") || routerServiceFileName.startsWith("/")) {
            routerServiceFileName = routerServiceFileName.substring(1);
        }

        routerServiceFileName = muleHome + routerServiceFileName;

        AsipDynamicRouteProcessor processor = null;
        if (isModifiedConfig(routerServiceFileName)) {
            try {
                processor = new AsipDynamicRouteProcessor(routerServiceFileName);
                dynamicRouteProcessor_CACHE.put(routerServiceFileName, processor);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), event, null);
            }
        }
        processor = new AsipDynamicRouteProcessor(dynamicRouteProcessor_CACHE.get(routerServiceFileName));
        Element confOutboundEndpoint = null;
        OutboundEndpoint[] eps = new OutboundEndpoint[3];
        int index_vm = 0;
        int index_http = 1;
        int index_https = 2;

        MuleEvent newEvent = event;
        while ((confOutboundEndpoint = processor.getNextValidOutboundElement(newEvent)) != null) {
            OutboundEndpoint ep = null;
            MessageProcessor em = null;
            for (int i = 0; i < routes.size(); i++) {
                em = routes.get(i);
                if (!(em instanceof OutboundEndpoint)) {
                    continue;
                }

                ep = (OutboundEndpoint) em;
                if (ep.getName().equals("vm-dummy")) {
                    eps[index_vm] = ep;
                } else if (ep.getName().equals("http-dummy")) {
                    eps[index_http] = ep;
                } else if (ep.getName().equals("https-dummy")) {
                    eps[index_https] = ep;
                }
            }
            if (ep == null) {
                throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), event, null);
            }
            if (confOutboundEndpoint.getAttributeValue("address").startsWith("vm")) {
                ep = eps[index_vm];
            } else if (confOutboundEndpoint.getAttributeValue("address").startsWith("https")) {
                ep = eps[index_https];
            } else if (confOutboundEndpoint.getAttributeValue("address").startsWith("http")) {
                ep = eps[index_http];
            }
            em = ep;
            MuleEndpointURI newUri;
            try {
                EndpointURI endpointURI = ep.getEndpointURI();
                newUri = new MuleEndpointURI(confOutboundEndpoint.getAttributeValue("address"), muleContext);
                newUri.initialise();
                if (endpointURI != null && !newUri.getScheme().equalsIgnoreCase(endpointURI.getScheme())) {
                    throw new CouldNotRouteOutboundMessageException(CoreMessages.schemeCannotChangeForRouter(ep
                            .getEndpointURI().getScheme(), newUri.getScheme()), event, ep);
                }
                // DynamicURIOutboundEndpoint dep = null;
                // dep = new DynamicURIOutboundEndpoint(ep);
                // dep.setEndpointURI(newUri);

                // EndpointURIEndpointBuilder endpointBuilder =
                // new EndpointURIEndpointBuilder("vm://kangaroo.vm.gdoiptest.channel", muleContext);
                // endpointBuilder.setResponseTimeout(ep.getResponseTimeout());
                // endpointBuilder.setExchangePattern(MessageExchangePattern.REQUEST_RESPONSE);
                // dep = new DynamicURIOutboundEndpoint(endpointBuilder.buildOutboundEndpoint());

                // EndpointURI endpointURIdep = dep.getEndpointURI();
                // logger.info("=========endpointURIdep=========" + endpointURIdep.getAddress() + "---path:"
                // + endpointURIdep.getPath());
                // result = sendRequest(event, event.getMessage(), dep, true);

                Connector connector = ep.getConnector();
                String doeName = this.getClass().getName();
                int responseTimeout = ep.getResponseTimeout();
                MessageExchangePattern exchangePattern = ep.getExchangePattern();
                try {
                    responseTimeout =
                            Integer.parseInt(confOutboundEndpoint.getAttributeValue("responseTimeout",
                                    "" + ep.getResponseTimeout()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String exchangePatternAttributeValue =
                        confOutboundEndpoint.getAttributeValue("exchange-pattern", "request-response");
                if (exchangePatternAttributeValue.equalsIgnoreCase("one-way")) {
                    exchangePattern = MessageExchangePattern.ONE_WAY;
                } else {
                    exchangePattern = MessageExchangePattern.REQUEST_RESPONSE;

                }
                DefaultOutboundEndpoint doe =
                        new DefaultOutboundEndpoint(ep.getConnector(), newUri, doeName, ep.getProperties(),
                                ep.getTransactionConfig(), false, exchangePattern, responseTimeout,
                                ep.getInitialState(), ep.getEncoding(), ep.getEndpointBuilderName(), muleContext,
                                ep.getRetryPolicyTemplate(), (String) ep.getProperty("PROPERTY_RESPONSE_PROPERTIES"),
                                ep.getMessageProcessorsFactory(), ep.getMessageProcessors(),
                                ep.getResponseMessageProcessors(), ep.isDisableTransportTransformer(), ep.getMimeType());

                processor.requestTransformer(event);
                event.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_TYPE", "request_bc");
                try {
                    if (AsipLogFilter.acceptFromSession(event.getMessage())) {
                        LocalMuleClient localMuleClient = null; // 用于调用VM
                        localMuleClient = event.getMuleContext().getClient();
                        Object logPayload = "";
                        if (newEvent != null && newEvent.getMessage() != null
                                && newEvent.getMessage().getPayload() != null) {
                            logPayload = newEvent.getMessage().getPayload();
                            if (logPayload instanceof InputStream) {
                                if (logPayload instanceof org.mule.transport.http.ReleasingInputStream) {
                                } else {
                                }
                                logPayload = createStringFromInputStream((InputStream) logPayload, doe.getEncoding());
                                newEvent.getMessage().setPayload(logPayload);
                            }
                        }
                        // message =
                        localMuleClient.send("vm://asip.vm.insert.interface.log.proxy.channel", logPayload,
                                IdaMsgUtils.getPropertyFormSession(event.getMessage()), write_log_timeout);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.toString());
                }
                Object logPayload = "";
                try {
                    result = sendRequest(event, newEvent.getMessage(), doe, true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    logger.error(e1.toString());
                    logPayload = e1.toString();
                }
                event.getMessage().setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_TYPE", "response_cd");
                try {
                    if (AsipLogFilter.acceptFromSession(event.getMessage())) {
                        LocalMuleClient localMuleClient = null; // 用于调用VM
                        localMuleClient = event.getMuleContext().getClient();
                        // message =
                        if (result != null && result.getMessage() != null && result.getMessage().getPayload() != null) {
                            logPayload = result.getMessage().getPayload();
                            if (logPayload instanceof InputStream) {
                                if (logPayload instanceof org.mule.transport.http.ReleasingInputStream) {

                                } else {
                                }
                                logPayload = createStringFromInputStream((InputStream) logPayload, doe.getEncoding());
                                result.getMessage().setPayload(logPayload);
                            }
                        }
                        localMuleClient.send("vm://asip.vm.insert.interface.log.proxy.channel", logPayload,
                                IdaMsgUtils.getPropertyFormSession(event.getMessage()), write_log_timeout);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.toString());
                }

                if (result == null) {
                    return result;
                }
                result = processor.responseTransformer(result);
                newEvent = result;
            } catch (MessagingException e) {
                e.printStackTrace();
                logger.error(e.toString());
                throw e;
            } catch (MuleException e) {
                e.printStackTrace();
                logger.error(e.toString());
                // throw new CouldNotRouteOutboundMessageException(event, em, e);
                throw new RoutingException(event, em, e);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.toString());
                // throw new CouldNotRouteOutboundMessageException(event, em, e);
                throw new RoutingException(event, em, e);
            }
            if (result != null) {
                if (logger.isDebugEnabled()) {
                    msgLogBuf.delete(0, msgLogBuf.length());
                    writeMessageProperties(msgLogBuf, result.getMessage());
                    if (logger.isDebugEnabled()) {
                        logger.debug(msgLogBuf.toString());
                    }
                }
            }
        }
        return result;
    }

    private void writeMessageProperties(final StringBuffer logEntry, final MuleMessage message) {
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
        logEntry.append("payload=[");
        // logEntry.append(message.getPayload());
        if (message.getPayload() == null) {
            logEntry.append("](null)");
        } else if (message.getPayload() instanceof String) {
            logEntry.append(message.getPayload());
            logEntry.append("](String)");
        } else if (message.getPayload() instanceof String[]) {
            String[] payload = (String[]) message.getPayload();
            for (int i = 0; i < payload.length; i++) {
                logEntry.append("\r\n--" + i + "---[");
                logEntry.append(payload[i]);
                logEntry.append("]");
            }
            logEntry.append("]");
        } else {
            logEntry.append(message.getPayload());
            logEntry.append("](" + message.getPayload().getClass().getName() + ")");
        }
        logEntry.append("]\r\n");
    }

    // 判断配置文件有没改变,如果有改变就重新加载配置
    private static boolean isModifiedConfig(String configFile) {
        if (configFile == null || configFile.equals("")) {
            return true;
        }
        if (dynamicRouteProcessor_CACHE.get(configFile) == null) {
            return true;
        }
        AsipDynamicRouteProcessor processor = dynamicRouteProcessor_CACHE.get(configFile);

        File f = new File(configFile);
        if (f.lastModified() > processor.getLastModified()) {
            return true;
        }
        return false;
    }

    protected String createStringFromInputStream(InputStream input, String outputEncoding) throws TransformerException {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            IOUtils.copy(input, byteOut);
            return byteOut.toString(outputEncoding);
        } catch (IOException e) {
            throw new TransformerException(CoreMessages.errorReadingStream(), e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                logger.warn("Could not close stream", e);
            }
        }
    }
}
