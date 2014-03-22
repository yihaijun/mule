/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.service;

public interface AsipServiceParamInf  {

    /**
     * @return the sender
     */
    public String getSender();
    /**
     * @param sender the sender to set
     */
    public void setSender(String sender);

    /**
     * @return the receiver
     */
    public String getReceiver();

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver);

    /**
     * @return the time
     */
    public String getTime();

    /**
     * @param time the time to set
     */
    public void setTime(String time);

    /**
     * @return the serviceName
     */
    public String getServiceName();

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName);
    /**
     * @return the msgType
     */
    public String getMsgType();

    /**
     * @param msgType the msgType to set
     */
    public void setMsgType(String msgType);
    /**
     * @return the msgId
     */
    public String getMsgId();

    /**
     * @param msgId the msgId to set
     */
    public void setMsgId(String msgId) ;

    /**
     * @return the appName
     */
    public String getAppName();

    /**
     * @param appName the appName to set
     */
    public void setAppName(String appName);

    /**
     * @return the errorCode
     */
    public String getErrorCode();

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode);

    /**
     * @return the errorInfo
     */
    public String getErrorInfo();

    /**
     * @param errorInfo the errorInfo to set
     */
    public void setErrorInfo(String errorInfo);

    /**
     * @return the simulateFlag
     */
    public String getSimulateFlag();

    /**
     * @param simulateFlag the simulateFlag to set
     */
    public void setSimulateFlag(String simulateFlag);

    /**
     * @return the keyword
     */
    public String getKeyword();

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword);

    public boolean parserBusinessServiceParam(String param);
    
//    public Map<String, Object> createMuleMessageProperties();
    /**
     * @return the paramNode
     */
//    public Node getParamNode();

    /**
     * @param paramNode the paramNode to set
     */
//    public void setParamNode(Node paramNode);

    /**
     * @return the businessServiceParam
     */
    public String getBusinessServiceParam();

    /**
     * @param businessServiceParam the businessServiceParam to set
     */
    public void setBusinessServiceParam(String businessServiceParam);

    /**
     * @return the cxf_operation
     */
    public String getCxf_operation();

    /**
     * @return the client_address
     */
    public String getClient_address();

    /**
     * @return the xml
     */
    public boolean isXml();

    public String selectNodeValue(String path);

    /**
     * 
     * <p>从业务xml串中提取子串</p>
     * @param xpath
     * @return
     */
    public String selectNodeAsXml(String xpath);

//    public String selectNodeValue(String path, Node node);

    public String toString();

}
