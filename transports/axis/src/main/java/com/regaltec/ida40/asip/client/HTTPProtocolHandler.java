package com.regaltec.ida40.asip.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.regaltec.asip.manager.api.client.AsipClientConfigItem;

/**
 * 
 * <p>ASIP客户端HTTP协议实现SOAP协议.</p>
 * <p>创建日期：2011-1-25 15:33:26</p>
 *
 * @author 朱志欧
 */
public class HTTPProtocolHandler extends AbstractProtocolHandler {

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * <p>构造函数</p>
     */
    public HTTPProtocolHandler() {
    }

    public String execute(String businessServiceName,String businessServiceParam,AsipClientConfigItem asipClientConfig) {
        if (asipClientConfig == null || StringUtils.isBlank(asipClientConfig.getIp())
                || asipClientConfig.getPort() == 0 || StringUtils.isBlank(asipClientConfig.getUri())) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0010","客户端异常，ASIP访问参数没有正确配置").toString();
        }
        String uri = asipClientConfig.getUri();
        HttpParams httpParams = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(httpParams,HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams,"UTF-8");
        HttpProtocolParams.setUserAgent(httpParams,"HttpCore/4.1");
        HttpProtocolParams.setUseExpectContinue(httpParams,true);
        HttpConnectionParams.setConnectionTimeout(httpParams,asipClientConfig.getConnectionTimeout());
        HttpConnectionParams.setTcpNoDelay(httpParams,true);
        HttpConnectionParams.setSocketBufferSize(httpParams,8192);
        HttpConnectionParams.setSoTimeout(httpParams,asipClientConfig.getReceiveTimeout());

        // HttpConnectionParams.setStaleCheckingEnabled(httpParams,true);

        HttpProcessor httpProcessor = new ImmutableHttpProcessor(
                new HttpRequestInterceptor[] { new RequestContent(),new RequestTargetHost(),new RequestConnControl(),
                        new RequestUserAgent(),new RequestExpectContinue() });
        DefaultHttpClientConnection httpConnection = null;
        Socket socket = null;
        try {
            httpConnection = new DefaultHttpClientConnection();
            // httpConnection.setSocketTimeout(asipClientConfig.getReceiveTimeout());
            HttpHost httpHost = new HttpHost(asipClientConfig.getIp(),asipClientConfig.getPort());
            socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(httpHost.getHostName(),httpHost.getPort()),asipClientConfig
                        .getConnectionTimeout());
                httpConnection.bind(socket,httpParams);
            } catch (IOException e) {
                logger.error(String.format("appName:%s,serviceName： %s,msgid:%s,asipClientConfig:,connect failed:",
                        businessServiceName,"","",asipClientConfig.toString(),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0020",
                        "无法连接到ASIP-" + httpHost.getHostName() + ":" + httpHost.getPort()).toString();
            } finally {
//                if (logger.isDebugEnabled()) {
//                    logger
//                            .debug(String
//                                    .format(
//                                            "appName:%s,serviceName:%s,msgid: %s,address:%s,isConnected: %s, isClosed: %s.",
//                                            businessServiceName,"","",asipClientConfig.toString(),
//                                            String.valueOf(socket.isConnected()),String.valueOf(socket.isClosed())
//                                            ));
//                }
            }
            HttpContext httpContext = new BasicHttpContext(null);
            httpContext.setAttribute(ExecutionContext.HTTP_CONNECTION,httpConnection);
            httpContext.setAttribute(ExecutionContext.HTTP_TARGET_HOST,httpHost);

            String soapMessage = buildSoapMessage(businessServiceName,businessServiceParam);
            HttpEntity httpEntity = new ByteArrayEntity(soapMessage.getBytes());
            ((ByteArrayEntity) httpEntity).setContentType("text/xml; charset=utf-8");
            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",uri);
            request.addHeader("SOAPAction","");
            request.setEntity(httpEntity);
            request.setParams(httpParams);
            HttpRequestExecutor httpExecutor = new HttpRequestExecutor();
            httpExecutor.preProcess(request,httpProcessor,httpContext);
            HttpResponse httpResponse = null;
            long rightNow = System.currentTimeMillis();
            try {
                httpResponse = httpExecutor.execute(request,httpConnection,httpContext);
            } catch (SocketTimeoutException e) {
                logger
                        .error(
                                String
                                        .format(
                                                "appName:%s,serviceName： %s,msgid:%s,address:%s,isConnected: %s,isClosed: %s,SocketTimeoutException：",
                                                businessServiceName,"","",asipClientConfig.toString(),
                                                String.valueOf(socket.isConnected()),String
                                                        .valueOf(socket.isClosed()),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0021",
                        "ASIP-" + httpHost.getHostName() + ":" + httpHost.getPort() + "超时未响应.").toString();
            } catch (SocketException e) {
                logger
                        .error(
                                String
                                        .format(
                                                "appName:%s,serviceName： %s,msgid:%s,address:%s,isConnected: %s,isClosed: %s,SocketException：",
                                                businessServiceName,"","",asipClientConfig.toString(),String.valueOf(socket.isConnected()),String
                                                        .valueOf(socket.isClosed()),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0030",
                        "ASIP-" + httpHost.getHostName() + ":" + httpHost.getPort() + "网络故障，网络不通或者连接中断了.").toString();
            } catch (NoHttpResponseException e) {
                logger
                        .error(
                                String
                                        .format(
                                                "appName:%s,serviceName： %s,msgid:%s,address:%s,isConnected: %s,isClosed: %s,NoHttpResponseException: %s.",
                                                businessServiceName,"","",asipClientConfig.toString(),String.valueOf(socket.isConnected()),String
                                                        .valueOf(socket.isClosed()),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0031",
                        "ASIP-" + httpHost.getHostName() + ":" + httpHost.getPort() + "没有响应结果，可能是响应超时或者网络中断.")
                        .toString();
            } finally {
                if (logger.isDebugEnabled()) {
                    logger.debug("appName:" + businessServiceName + ",serviceName:,msgid:,address："
                            + asipClientConfig.toString() + ",HttpCore call ASIP used "
                            + (System.currentTimeMillis() - rightNow) + " ms.");
                }
            }
            httpResponse.setParams(httpParams);
            httpExecutor.postProcess(httpResponse,httpProcessor,httpContext);
            httpEntity = httpResponse.getEntity();
            String responseText = null;
            try {
                responseText = httpEntity == null ? null : EntityUtils.toString(httpEntity,"UTF-8");
            } catch (IOException e) {
                logger.error(String.format("appName:%s,serviceName： %s,msgid:%s,HTTP return error result.asipClientConfig:%s,Exception:%s",
                        businessServiceName,"","",asipClientConfig.toString(),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0032",
                        "无法处理ASIP-" + httpHost.getHostName() + ":" + httpHost.getPort() + "HTTP返回的结果.").toString();
            }
            return parseSoapResponse(responseText,new RequestContext(asipClientConfig,businessServiceName))
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("appName:%s,serviceName:%s,msgid:%s,call WebService happen Exception:%s",
                    businessServiceName,"","",e.toString()));
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0030",
                    "调用ASIP-" + asipClientConfig.toString() + "WebService时出现异常.").toString();
        } finally {
            if (httpConnection != null) {
                try {
                    httpConnection.shutdown();
                } catch (IOException e) {// 如果出现非IOException，则下面的if语句是无作用的，但httpConnection.shutdown()有关闭socket的.
                    logger.error(String.format(
                            "appName:%s,serviceName： %s,msgid:%s,httpConnection.shutdown IOException：",
                            businessServiceName,"","",e.toString()));
                }
            }
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error(String.format("appName:%s,serviceName： %s,msgid:%s,socket.close IOException：",
                            businessServiceName,"","",e.toString()));
                }
            } else {
                // logger.info("socket已经由httpConnection.shutdown()关闭了.");
            }
        }
    }

    /**
     * <p>以HTTP方式调用ASIP的SOAP服务，首次连接到ASIP失败时，自动尝试ASIP的备用地址.</p>
     * 
     * @param context 请求上下文
     * @return soap1.2格式的响应消息包
     */
    public ResponseMessage execute(RequestContext context) {
        AsipClientConfigItem asipClientConfig = context.getAsipClientConfigItem();// 当前配置为主配置
        if (asipClientConfig == null || StringUtils.isBlank(asipClientConfig.getIp())
                || asipClientConfig.getPort() == 0 || StringUtils.isBlank(asipClientConfig.getUri())) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0010","客户端异常，ASIP访问参数没有正确配置",context);
        }
        String uri = asipClientConfig.getUri();
        HttpParams httpParams = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(httpParams,HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams,"UTF-8");
        HttpProtocolParams.setUserAgent(httpParams,"HttpCore/4.1");
        HttpProtocolParams.setUseExpectContinue(httpParams,true);
        HttpConnectionParams.setConnectionTimeout(httpParams,asipClientConfig.getConnectionTimeout());
        HttpConnectionParams.setTcpNoDelay(httpParams,true);
        HttpConnectionParams.setSocketBufferSize(httpParams,8192);
        HttpConnectionParams.setSoTimeout(httpParams,asipClientConfig.getReceiveTimeout());

        HttpProcessor httpProcessor = new ImmutableHttpProcessor(
                new HttpRequestInterceptor[] { new RequestContent(),new RequestTargetHost(),new RequestConnControl(),
                        new RequestUserAgent(),new RequestExpectContinue() });
        DefaultHttpClientConnection httpConnection = null;
        Socket socket = null;
        String messageId = context.getRequestMessage().getMsgId();
        String moduleName = context.getBusinessModuleName();
        String serviceName = context.getRequestMessage().getServiceName();
        try {
            httpConnection = new DefaultHttpClientConnection();
            // httpConnection.setSocketTimeout(asipClientConfig.getReceiveTimeout());
            HttpHost httpHost = new HttpHost(asipClientConfig.getIp(),asipClientConfig.getPort());
            socket = new Socket();
            try {
                try {
                    socket.connect(new InetSocketAddress(httpHost.getHostName(),httpHost.getPort()),asipClientConfig
                            .getConnectionTimeout());
                } catch (Exception e) {
                    logger.error(String.format("appName:%s,serviceName： %s,msgid:%s,connect(%s:%d)failed：",
                            moduleName,serviceName,messageId,asipClientConfig.getIp(),asipClientConfig.getPort()),
                            e);
                }
                if (socket.isConnected()) {
                    httpConnection.bind(socket,httpParams);
                } else {
                    if (StringUtils.isNotBlank(asipClientConfig.getIp2())) {
                        try {
                            socket.close();
                        } catch (Exception e2) {

                        }
                        httpHost = new HttpHost(asipClientConfig.getIp2(),asipClientConfig.getPort2());
                        uri = asipClientConfig.getUri2();
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format(
                                    "appName:%s,serviceName:%s,msgid:%s,Try to connect address2(%s:%d).",
                                    moduleName,serviceName,messageId,asipClientConfig.getIp2(),asipClientConfig
                                            .getPort2()));
                        }
                        try {
                            socket = new Socket();
                            socket.connect(new InetSocketAddress(httpHost.getHostName(),httpHost.getPort()),
                                    asipClientConfig.getConnectionTimeout());
                            httpConnection.bind(socket,httpParams);
                        } catch (IOException e1) {
                            logger.error(String.format(
                                    "appName:%s,serviceName： %s,msgid:%s,connect address2(%s:%d)failed：",
                                    moduleName,serviceName,messageId,asipClientConfig.getIp2(),asipClientConfig
                                            .getPort2()),e1);
                            return AsipClient.buildExceptivelyResponseMessage("ASIP-0020","无法连接到ASIP-"
                                    + httpHost.getHostName() + ":" + httpHost.getPort(),context);
                        }
                    } else {
                        return AsipClient.buildExceptivelyResponseMessage("ASIP-0020","无法连接到ASIP-"
                                + httpHost.getHostName() + ":" + httpHost.getPort(),context);
                    }
                }
            } finally {
//                if (logger.isDebugEnabled()) {
//                    logger
//                            .debug(String
//                                    .format(
//                                            "appName:%s,serviceName:%s,msgid: %s,address:%s:%d,isConnected: %s, isClosed: %s,connectionTimeout:%d,receiveTimeout:%d.",
//                                            moduleName,serviceName,messageId,httpHost.getHostName(),httpHost
//                                                    .getPort(),String.valueOf(socket.isConnected()),String
//                                                    .valueOf(socket.isClosed()),asipClientConfig
//                                                    .getConnectionTimeout(),asipClientConfig.getReceiveTimeout()));
//                }
            }
            HttpContext httpContext = new BasicHttpContext(null);
            httpContext.setAttribute(ExecutionContext.HTTP_CONNECTION,httpConnection);
            httpContext.setAttribute(ExecutionContext.HTTP_TARGET_HOST,httpHost);

            String soapMessage = buildSoapMessage(context);
            HttpEntity httpEntity = new ByteArrayEntity(soapMessage.getBytes());
            ((ByteArrayEntity) httpEntity).setContentType("text/xml; charset=utf-8");
            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",uri);
            request.addHeader("SOAPAction","");
            request.setEntity(httpEntity);
            request.setParams(httpParams);
            HttpRequestExecutor httpExecutor = new HttpRequestExecutor();
            httpExecutor.preProcess(request,httpProcessor,httpContext);
            HttpResponse httpResponse = null;
            long rightNow = System.currentTimeMillis();
            try {
                httpResponse = httpExecutor.execute(request,httpConnection,httpContext);
            } catch (SocketTimeoutException e) {
                logger
                        .error(
                                String
                                        .format(
                                                "appName:%s,serviceName： %s,msgid:%s,address:%s:%d,isConnected: %s,isClosed: %s,SocketTimeoutException:%s",
                                                moduleName,serviceName,messageId,httpHost.getHostName(),httpHost
                                                        .getPort(),String.valueOf(socket.isConnected()),String
                                                        .valueOf(socket.isClosed()),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0021","ASIP-" + httpHost.getHostName() + ":"
                        + httpHost.getPort() + "超时未响应.",context);
            } catch (SocketException e) {
                logger
                        .error(
                                String
                                        .format(
                                                "appName:%s,serviceName： %s,msgid:%s,address:%s:%d,isConnected: %s,isClosed: %s,SocketException:%s",
                                                moduleName,serviceName,messageId,httpHost.getHostName(),httpHost
                                                        .getPort(),String.valueOf(socket.isConnected()),String
                                                        .valueOf(socket.isClosed()),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0030","ASIP-" + httpHost.getHostName() + ":"
                        + httpHost.getPort() + "网络故障，网络不通或者连接中断了.",context);
            } catch (NoHttpResponseException e) {
                logger
                        .error(
                                String
                                        .format(
                                                "appName:%s,serviceName： %s,msgid:%s,address:%s:%d,isConnected: %s,isClosed: %s,NoHttpResponseException: The target server failed to respond:%s",
                                                moduleName,serviceName,messageId,httpHost.getHostName(),httpHost
                                                        .getPort(),String.valueOf(socket.isConnected()),String
                                                        .valueOf(socket.isClosed()),e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0031","ASIP-" + httpHost.getHostName() + ":"
                        + httpHost.getPort() + "没有响应结果，可能是响应超时或者网络中断.",context);
            } finally {
                if (logger.isDebugEnabled()) {
                    logger.debug("appName:" + moduleName + ",serviceName:" + serviceName + ",msgid:" + messageId
                            + ",address：(" + httpHost.getHostName() + ":" + httpHost.getPort()
                            + "),HttpCore call ASIP used " + (System.currentTimeMillis() - rightNow) + " ms.");
                }
            }
            httpResponse.setParams(httpParams);
            httpExecutor.postProcess(httpResponse,httpProcessor,httpContext);
            httpEntity = httpResponse.getEntity();
            String responseText = null;
            try {
                responseText = httpEntity == null ? null : EntityUtils.toString(httpEntity,"UTF-8");
            } catch (IOException e) {
                logger.error(String.format("appName:%s,serviceName： %s,msgid:%s,HTTP return error result:%s",
                        moduleName,serviceName,messageId,e.toString()));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0032","无法处理ASIP-" + httpHost.getHostName()
                        + ":" + httpHost.getPort() + "HTTP返回的结果.",context);
            }
            return parseSoapResponse(responseText,context);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("appName:%s,serviceName:%s,msgid:%s,call WebService happen Exception:%s",
                    moduleName,serviceName,messageId,e.toString()));
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0030","调用ASIP-" + asipClientConfig.toString()
                    + "WebService时出现异常.",context);
        } finally {
            if (httpConnection != null) {
                try {
                    httpConnection.shutdown();
                } catch (IOException e) {// 如果出现非IOException，则下面的if语句是无作用的，但httpConnection.shutdown()有关闭socket的.
                    logger.error(String.format(
                            "appName:%s,serviceName： %s,msgid:%s,httpConnection.shutdown IOException:%s",moduleName,
                            serviceName,messageId,e.toString()));
                }
            }
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error(String.format("appName:%s,serviceName： %s,msgid:%s,socket.close IOException:%s",
                            moduleName,serviceName,messageId,e.toString()));
                }
            } else {
                // logger.info("socket已经由httpConnection.shutdown()关闭了.");
            }
        }
    }

    /**
     * <p>构造soap请求消息包.</p>
     * @param context 请求消息上下文
     * @return 符合soap1.2的消息包
     */
    private String buildSoapMessage(RequestContext context) {
        return buildSoapMessage(context.getBusinessModuleName(),context.getRequestMessage().toString());
    }

    // private String buildSoapMessage(String businessServiceName,String businessServiceParam) {
    // StringBuffer soapMessage = new StringBuffer();
    // soapMessage.append("<soap:Envelope xmlns:soap=\"").append(SOAP11_NS).append("\">");
    // soapMessage.append("<soap:Body>");
    // soapMessage.append("<call xmlns=\"").append(ASIP_NS).append("\">");
    // soapMessage.append("<businessServiceName xmlns=\"\"><![CDATA[");
    // soapMessage.append(businessServiceName);
    // soapMessage.append("]]></businessServiceName>");
    // soapMessage.append("<businessServiceParam xmlns=\"\"><![CDATA[");
    // soapMessage.append(businessServiceParam);
    // soapMessage.append("]]></businessServiceParam>");
    // soapMessage.append("<requestContextName xmlns=\"\"></requestContextName>");
    // soapMessage.append("</call>");
    // soapMessage.append("</soap:Body>");
    // soapMessage.append("</soap:Envelope>");
    // return soapMessage.toString();
    // }

    private String buildSoapMessage(String businessServiceName,String businessServiceParam) {
        StringBuffer soapMessage = new StringBuffer();
        soapMessage.append("<soap:Envelope xmlns:soap=\"" + SOAP11_NS + "\">");
        soapMessage.append("<soap:Body>");
        soapMessage.append("<call xmlns=\"" + ASIP_NS + "\">");
        soapMessage.append("<businessServiceName xmlns=\"\">").append(businessServiceName).append(
                "</businessServiceName>");
        soapMessage.append("<businessServiceParam xmlns=\"\">").append(
                StringEscapeUtils.escapeXml(businessServiceParam)).append("</businessServiceParam>");
        soapMessage.append("<requestContextName xmlns=\"\">").append("").append("</requestContextName>");
        soapMessage.append("</call>");
        soapMessage.append("</soap:Body>");
        soapMessage.append("</soap:Envelope>");
        return soapMessage.toString();

    }
}
