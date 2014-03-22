/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.mule.RequestContext;
import org.mule.api.MuleMessage;
import org.mule.module.xml.util.XMLUtils;
import org.w3c.dom.Node;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-12-27 下午07:18:56</p>
 *
 * @author yihaijun
 */
public class AsipServiceParam  implements AsipServiceParamInf,Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3810893290923304674L;

    private Node paramNode = null;
    private String businessServiceParam = "";

    /**
     * 发送方的系统编码
     */
    private String sender = "";
    /**
     * 接收方的系统码
     */
    private String receiver = "";
    /**
     * 调用时间
     */
    private String time = "";
    /**
     * 服务名称
     */
    private String serviceName = "";
    /**
     *  消息类型（request请求、response响应）
     */
    private String msgType = "";
    /**
     * 消息唯一标识，请求与响应消息的msg_id相同
     */
    private String msgId = com.regaltec.asip.utils.IdaMsgUtils.getUUID();
    /**
     * 接口组名称
     */
    private String appName = "";
    /**
     * 错误代码
     */
    private String errorCode = "";
    /**
     * 错误描述
     */
    private String errorInfo = "";

    /**
     * 是否摸拟测试消息
     */
    private String simulateFlag = "";
    /**
     * 业务关键字
     */
    private String keyword = "";

    private String cxf_operation = "";
    private String client_address = "";

    private boolean xml = false;

    public AsipServiceParam() {
        super();
    }

    public AsipServiceParam(String businessServiceParam) {
        super();
        this.businessServiceParam = businessServiceParam;
        this.parserBusinessServiceParam(this.businessServiceParam);
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the msgType
     */
    public String getMsgType() {
        return msgType;
    }

    /**
     * @param msgType the msgType to set
     */
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    /**
     * @return the msgId
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * @param msgId the msgId to set
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorInfo
     */
    public String getErrorInfo() {
        return errorInfo;
    }

    /**
     * @param errorInfo the errorInfo to set
     */
    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    /**
     * @return the simulateFlag
     */
    public String getSimulateFlag() {
        return simulateFlag;
    }

    /**
     * @param simulateFlag the simulateFlag to set
     */
    public void setSimulateFlag(String simulateFlag) {
        this.simulateFlag = simulateFlag;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean parserBusinessServiceParam(String param) {
        String simpleParam =
                "<service><head><sender/><receiver/><time/><service_name/><msg_type/><msg_id/><simulate_flag>DEBUG</simulate_flag></head></service>";
        xml = false;
        try {
            businessServiceParam = param;
            if (!param.equals("businessServiceParam") && !param.equals("test") && !param.startsWith("DEBUGAsip")) {
                paramNode = (Node) XMLUtils.toW3cDocument(param);
                xml = true;

            } else {
                paramNode = (Node) XMLUtils.toW3cDocument(simpleParam);
                xml = false;
            }
            sender = XMLUtils.selectValue("/service/head/sender", paramNode);
            receiver = XMLUtils.selectValue("/service/head/receiver", paramNode);
            time = XMLUtils.selectValue("/service/head/time", paramNode);
            serviceName = XMLUtils.selectValue("/service/head/service_name", paramNode);
            msgType = XMLUtils.selectValue("/service/head/msg_type", paramNode);
            msgId = XMLUtils.selectValue("/service/head/msg_id", paramNode);
            simulateFlag = XMLUtils.selectValue("/service/head/simulate_flag", paramNode);
            keyword =  XMLUtils.selectValue("/service/head/keyword", paramNode);
            return true;
        } catch (Exception e) {
            xml = false;
            try {
                paramNode = (Node) XMLUtils.toW3cDocument(simpleParam);
            } catch (Exception e1) {
                // e1.printStackTrace();
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createMuleMessageProperties() {
        Properties messageProperties = new Properties();
        messageProperties.put("IDA40_ASIP_MSG_PARAM_NODE", paramNode);
        messageProperties.put("ida40_asip_msg_param_asip", this);

        try {
            MuleMessage requestMuleMessage = RequestContext.getEventContext().getMessage();
            Set<String> set = requestMuleMessage.getOutboundPropertyNames();
            Iterator<String> it = set.iterator();
            String key;
            String value = "";
            while (it.hasNext()) {
                key = it.next();
                value = requestMuleMessage.getOutboundProperty(key);
                messageProperties.put(key, value);
            }
            String preCxf_operation = "{http://service.asip.regaltec.com/}";
            cxf_operation =
                    requestMuleMessage.getInvocationProperty("cxf_operation", preCxf_operation + requestMuleMessage.getInboundProperty("http.method","method")).substring(
                            preCxf_operation.length());
            messageProperties.put("cxf_operation", cxf_operation);
            String asipRemoteClientAddress =
                    requestMuleMessage.getInboundProperty("MULE_REMOTE_CLIENT_ADDRESS", "/0.0.0.0:0");
            if (asipRemoteClientAddress.equals("/0.0.0.0:0")) {
                if (requestMuleMessage.getOutboundProperty("ASIP_REMOTE_CLIENT_ADDRESS") != null) {
                    asipRemoteClientAddress = requestMuleMessage.getOutboundProperty("ASIP_REMOTE_CLIENT_ADDRESS");
                }
            }
            messageProperties.put("ASIP_REMOTE_CLIENT_ADDRESS", asipRemoteClientAddress);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        if (sender != null && !sender.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_SENDER", sender);
        }
        if (receiver != null && !receiver.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_RECEIVER", receiver);
        }
        if (time != null && !time.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_TIME", com.regaltec.asip.utils.DateUtils.formatDateString(time));
        }
        if (serviceName != null && !serviceName.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", serviceName);
        } else {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", cxf_operation);
        }
        if (msgType != null && !msgType.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_MSG_TYPE", msgType);
        }
        if (msgId != null && !msgId.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_MSG_ID", msgId);
        }
        if (appName != null && !appName.equals("")) {
            if (messageProperties.get("IDA40_ASIP_MSG_HEAD_APP_NAME") != null
                    && !messageProperties.get("IDA40_ASIP_MSG_HEAD_APP_NAME").toString().startsWith("UNKNOW")) {
                // 这种情况是指为了兼容旧系统发布各种服务时已指定了接口组,所以直接调用AsipServiceImpl的call方法时，不再采用第一个参数来当做接口组
            } else {
                messageProperties.put("IDA40_ASIP_MSG_HEAD_APP_NAME", appName);
            }
        }
        if (simulateFlag != null && !simulateFlag.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG", simulateFlag);
        }
        if (errorCode != null && !errorCode.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", errorCode);
        }
        if (errorInfo != null && !errorInfo.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", errorInfo);
        }
        if (keyword != null && !keyword.equals("")) {
            messageProperties.put("IDA40_ASIP_MSG_HEAD_KEYWORD", keyword);
        }
        messageProperties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_AB", "0");
        messageProperties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_BC", "0");
        messageProperties.put("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_CD", "0");
        return (Map) messageProperties;
    }

    /**
     * @return the paramNode
     */
    public Node getParamNode() {
        return paramNode;
    }

    /**
     * @param paramNode the paramNode to set
     */
    public void setParamNode(Node paramNode) {
        this.paramNode = paramNode;
    }

    /**
     * @return the businessServiceParam
     */
    public String getBusinessServiceParam() {
        return businessServiceParam;
    }

    /**
     * @param businessServiceParam the businessServiceParam to set
     */
    public void setBusinessServiceParam(String businessServiceParam) {
        this.businessServiceParam = businessServiceParam;
    }

    /**
     * @return the cxf_operation
     */
    public String getCxf_operation() {
        return cxf_operation;
    }

    /**
     * @return the client_address
     */
    public String getClient_address() {
        return client_address;
    }

    /**
     * @return the xml
     */
    public boolean isXml() {
        return xml;
    }

    public String selectNodeValue(String path) {
        return selectNodeValue(path, paramNode);
    }

    /**
     * 
     * <p>从业务xml串中提取子串</p>
     * @param xpath
     * @return
     */
    public String selectNodeAsXml(String xpath) {
        try {
            Document doc = DocumentHelper.parseText(this.businessServiceParam);
            org.dom4j.Node n = doc.selectSingleNode(xpath);
            return n.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String selectNodeValue(String path, Node node) {
        try {
            return org.mule.module.xml.util.XMLUtils.selectValue(path, node);
        } catch (Exception e) {
            return "";
        }
    }

    public String toString() {
        return businessServiceParam;
    }

    /**
     * 
     * <p>格式化表达时间的字符串</p>
     * @param date  表达时间的字符串
     * @return 表达时间的字符串
     */
    public static String formatDateString(String dateStr) {
        Date date = null;
        try {
            date = org.mule.util.DateUtils.getDateFromString(dateStr, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            date = new Date();
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
    }
}
