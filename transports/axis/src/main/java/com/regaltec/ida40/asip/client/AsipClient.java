/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.ida40.asip.client;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.regaltec.asip.manager.api.client.AsipClientConfig;
import com.regaltec.asip.manager.api.client.AsipClientConfigItem;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-5-11 下午02:34:31</p>
 *
 * @author yihaijun
 */
public class AsipClient implements ApplicationContextAware {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    // private ApplicationContext applicationContext;

    private AsipClientConfigItem asipClientConfig;

    /**
     * 协议处理器
     */
    private ProtocolHandler protocolHandler = null;

    /**
     * 
     * 构造函数
     */
    public AsipClient() {
    }

    /**
     * 
     * 构造函数，默认使用SOAPProtocolHandler。
     * @param ip ASIPip
     * @param port ASIP端口
     * @param uri ASIP接收服务uri，根据具体协议决定此尝试是否必须，如tcp协议可能就不需要设置该参数
     */
    public AsipClient(String ip, int port, String uri) {
        this(ip, port, uri, new SOAPProtocolHandler());
    }

    /**
     * 
     * 构造函数
     * @param ip ASIPip
     * @param port ASIP端口
     * @param uri ASIP接收服务uri，根据具体协议决定此尝试是否必须，如tcp协议可能就不需要设置该参数
     * @param protocolHandler 协议处理器
     */
    public AsipClient(String ip, int port, String uri, ProtocolHandler protocolHandler) {
        this.asipClientConfig = new AsipClientConfigItem(ip, port, uri);
        this.protocolHandler = protocolHandler;
    }

    /**
     * 
     * 构造函数，默认使用SOAPProtocolHandler。
     * @param asipConfig ASIP配置信息
     */
    public AsipClient(AsipClientConfigItem asipClientConfig) {
        // this(asipClientConfig, new SOAPProtocolHandler());
        this(asipClientConfig, new HTTPProtocolHandler());
    }

    /**
     * 
     * 构造函数
     * @param protocolHandler 协议处理器
     */
    public AsipClient(ProtocolHandler protocolHandler) {
        this.protocolHandler = protocolHandler;
    }

    /**
     * 
     * 构造函数
     * @param asipConfig ASIP配置信息
     * @param protocolHandler 协议处理器
     */
    public AsipClient(AsipClientConfigItem asipConfig, ProtocolHandler protocolHandler) {
        this.asipClientConfig = asipConfig;
        this.protocolHandler = protocolHandler;
    }

    public String call(String businessServiceParam) {
        try {
            RequestMessage  requestMessage = RequestMessage.newInstance(businessServiceParam);
            String serviceName = requestMessage.getServiceName();
            if(serviceName == null || serviceName.equals("")){
                return call("NOSERVICENAME", businessServiceParam, "");
            }
            return call(serviceName, businessServiceParam, "");
        } catch (Exception e) {
            return call("PARAMNOTXML", businessServiceParam, "");
        }
    }

    public String call(String businessModuleName, String businessServiceParam, String requestContextName) {
        long beginTime = System.currentTimeMillis();
        RequestMessage requestMessage = null;
        try {
            requestMessage = RequestMessage.newInstance(businessServiceParam);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("call(" + businessModuleName + ",UNKNOW,) begin.request is [" + businessServiceParam
                                + "]");
            }
            if (asipClientConfig == null) {
                asipClientConfig = AsipClientConfig.getAsipClientConfigItem(businessModuleName, null);
            }
            String result = "";
            String errorCode = "ASIP-0000";
            // AsipNodeClient nodeClient = new AsipNodeClient(asipClientConfig);
            // result =nodeClient.call(businessModuleName, businessServiceParam, requestContextName);
            if (protocolHandler == null) {
                try {
                    String protocolHandlerValue = AsipClientConfig.getProtocolhandlerclass();
                    try {
                        protocolHandler = (ProtocolHandler) Class.forName(protocolHandlerValue).newInstance();
                    } catch (InstantiationException e2) {
                        e2.printStackTrace();
                        errorCode = "ASIP-0010";
                        ResponseMessage responseInfo = new ResponseMessage("", "",
                                com.regaltec.asip.manager.api.util.DateUtil.now(), "UNKNOW", "response", "", "",
                                errorCode, "ASIPCLIENT.PROTOCOLHANDLERCLASS配置错误");
                        result = responseInfo.toString();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    errorCode = "ASIP-0010";
                    ResponseMessage responseInfo = new ResponseMessage("", "",
                            com.regaltec.asip.manager.api.util.DateUtil.now(), "UNKNOW", "response", "", "", errorCode,
                            "ASIPCLIENT.PROTOCOLHANDLERCLASS配置错误");
                    result = responseInfo.toString();
                }
            }
            long finshedLoadConfigTime = System.currentTimeMillis();
            if (result.equals("")) {
                try {
                    result = protocolHandler.execute(businessModuleName, businessServiceParam, asipClientConfig);
                } catch (Exception e3) {
                    result = buildExceptivelyResponseMessage("ASIP-0030",
                            "客户端异常,具体错误原因不明确,以下是异常堆栈信息：\n" + getExceptionTrace(e3)).toString();
                }
            }
            createCallReport(beginTime, finshedLoadConfigTime, errorCode, businessModuleName, "UNKNOW", "",
                    businessServiceParam, result, 0, 0, 0);
            return result;
        }
        ResponseMessage response = call(businessModuleName, requestMessage);
        return response.toString();
    }

    /**
     * 
     * <p>将消息体发送到ASIP接收端点，是否同步由protocolHandler决定。</p>
     * <p>调用该方法永远不会抛出异常，检查是否出错可以使用如下方式：</p>
     * <code>
     * ResponseMessage rm = new AsipClient(ip,port,uri).call("predeal",requestMessage);
     * if(!rm.isFault()){
     *    put your code here
     * }else{
     *    throw new BOException(rm.getErrorInfo);
     * }
     * </code>
     * @param businessModuleName 业务模块名称
     * @param requestMessage 请求消息
     * @return 响应消息
     * 错误代码定义:
        ASIP-0000       接口调用正常
        ASIP-0010       接口平台访问参数没有配置
        ASIP-0011       调用接口平台前异常
        ASIP-0020       接口平台连接失败
        ASIP-0021       接口平台超时未响应
        ASIP-0030       调用接口平台异常
        ASIP-0031       接口平台响应空值
        ASIP-0032       调用接口平台后异常
     */
    public ResponseMessage call(String businessModuleName, RequestMessage requestMessage) {
        if (StringUtils.isBlank(businessModuleName) || requestMessage == null) {
            throw new IllegalArgumentException();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("call(" + businessModuleName + "," + requestMessage.getServiceName() + ") begin.request is ["
                    + requestMessage.toString() + "]");
        }
        long beginTime = System.currentTimeMillis();
        if (asipClientConfig == null) {
            asipClientConfig = AsipClientConfig.getAsipClientConfigItem(businessModuleName, requestMessage
                    .getServiceName());
        }
        ResponseMessage responseMessage = null;
        if (protocolHandler == null) {
            try {
                String protocolHandlerValue = AsipClientConfig.getProtocolhandlerclass();
                try {
                    protocolHandler = (ProtocolHandler) Class.forName(protocolHandlerValue).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    responseMessage = new ResponseMessage("", "", com.regaltec.asip.manager.api.util.DateUtil.now(),
                            "UNKNOW", "response", "", "", "ASIP-0010", "ASIPCLIENT.PROTOCOLHANDLERCLASS配置错误");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                responseMessage = new ResponseMessage("", "", com.regaltec.asip.manager.api.util.DateUtil.now(),
                        "UNKNOW", "response", "", "", "ASIP-0010", "ASIPCLIENT.PROTOCOLHANDLERCLASS配置错误");
            }
        }

        RequestContext context = null;
        try {
            context = new RequestContext(asipClientConfig, businessModuleName);
        } catch (Exception e) {
            responseMessage = buildExceptivelyResponseMessage("ASIP-0010", "调用ASIP前异常,具体错误原因不明确,以下是异常堆栈信息：\n"
                    + getExceptionTrace(e), context);
        }
        try {
            requestMessage.setMsgId(context.getRequestId());
            context.setRequestMessage(requestMessage);
        } catch (Exception e) {
            responseMessage = buildExceptivelyResponseMessage("ASIP-0011", "调用ASIP前异常,具体错误原因不明确,以下是异常堆栈信息：\n"
                    + getExceptionTrace(e), context);
        }
        long finshedLoadConfigTime = System.currentTimeMillis();
        try {
            if (responseMessage == null) {
                responseMessage = protocolHandler.execute(context);
            }
        } catch (Exception e) {
            responseMessage = buildExceptivelyResponseMessage("ASIP-0030", "客户端异常,具体错误原因不明确,以下是异常堆栈信息：\n"
                    + getExceptionTrace(e), context);// 这个地方还要把ASIP-0020和ASIP-0030分开
        }
        createCallReport(beginTime, finshedLoadConfigTime, responseMessage.getErrorCode(), businessModuleName,
                requestMessage.getServiceName(), requestMessage.getMsgId(), requestMessage.toString(), responseMessage
                        .toString(), responseMessage.getTimeConsumingAB(), responseMessage.getTimeConsumingBC(),
                responseMessage.getTimeConsumingCD());
        return responseMessage;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // this.applicationContext = applicationContext;
    }

    /**
     * <p>创建一个例外的响应消息</p>
     * @param errorCode 错误码
     * @param errorInfo 错误描述
     * @param context 请求上下文
     * @return 例外的响应消息对象
     */
    protected static ResponseMessage buildExceptivelyResponseMessage(String errorCode, String errorInfo,
            RequestContext context) {
        ResponseMessage responseMessage = new ResponseMessage();
        /**
         * 将请求信息中的receiver和sender交换作为响应的receiver和sender
         */
        responseMessage.setReceiver(context.getRequestMessage().getSender());
        responseMessage.setSender(context.getRequestMessage().getReceiver());
        responseMessage.setServiceName(context.getRequestMessage().getServiceName());
        responseMessage.setMsgType("response");
        responseMessage.setMsgId(context.getRequestMessage().getMsgId()); // 取请求消息id作为响应消息id以保证消息一致
        responseMessage.setTime(ResponseMessage.getCurrentDate());
        responseMessage.setErrorCode(errorCode);
        responseMessage.setErrorInfo(errorInfo);
        return responseMessage;
    }

    /**
     * <p>创建一个例外的响应消息</p>
     * @param errorCode 错误码
     * @param errorInfo 错误描述
     * @param context 请求上下文
     * @return 例外的响应消息对象
     */
    protected static ResponseMessage buildExceptivelyResponseMessage(String errorCode, String errorInfo) {
        ResponseMessage responseMessage = new ResponseMessage();
        /**
         * 将请求信息中的receiver和sender交换作为响应的receiver和sender
         */
        responseMessage.setReceiver("");
        responseMessage.setSender("");
        responseMessage.setServiceName("");
        responseMessage.setMsgType("response");
        responseMessage.setTime(ResponseMessage.getCurrentDate());
        responseMessage.setMsgId(""); // 取请求消息id作为响应消息id以保证消息一致
        responseMessage.setErrorCode(errorCode);
        responseMessage.setErrorInfo(errorInfo);
        return responseMessage;
    }

    /**
     * <p>获取异常堆栈信息</p>
     * @param e 异常对象
     * @return 异常堆栈信息
     */
    protected static String getExceptionTrace(Throwable e) {
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } else {
            return "No Exception";
        }
    }

    private boolean isWarn(long callTime, long thresholdTime, String errorCode) {
        if(errorCode==null){
            return true;
        }
        if (callTime < thresholdTime && (errorCode.equals("ASIP-0000") || errorCode.equals("ASIP-2050"))) {
            return false;
        }
        return true;
    }

    private String createCallReport(long beginTime, long finshedLoadConfigTime, String errorCode, String appName,
            String serviceName, String msgId, String inParam, String result, long timeConsumingAB,
            long timeConsumingBC, long timeConsumingCD) {
        long endTime = System.currentTimeMillis();
        long loadConfigTime = finshedLoadConfigTime - beginTime;
        long callTime = endTime - finshedLoadConfigTime;
        long time = endTime - beginTime;
        int thresholdTime = AsipClientConfig.getThresholdtime();

        if (!isWarn(callTime, thresholdTime, errorCode) && !logger.isDebugEnabled()) {
            return "";
        }

        StringBuffer callReport = new StringBuffer();
        callReport.delete(0, callReport.length());
        callReport.append("call(");
        callReport.append(appName);
        callReport.append(",");
        callReport.append(serviceName);
        callReport.append(",");
        callReport.append(msgId);
        callReport.append(") time: ");
        callReport.append(String.valueOf(time));
        callReport.append("ms");
        if (callTime > thresholdTime) {
            callReport.append(">");
            callReport.append(String.valueOf(thresholdTime));
            callReport.append("ms");
        }
        callReport.append("(");
        callReport.append(String.valueOf(loadConfigTime));
        callReport.append("+");
        callReport.append(String.valueOf(callTime));
        callReport.append(")");
        callReport.append("\r\nrequest is [");
        callReport.append(inParam);
        callReport.append("]");
        callReport.append("\r\nresponse is [");
        callReport.append(result);
        callReport.append("]\r\nasipClientConfig is");
        callReport.append(asipClientConfig.toString());

        if (isWarn(callTime, thresholdTime, errorCode)) {
            logger.warn(callReport.toString());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(callReport.toString());
            }
        }
        return callReport.toString();
    }
}
