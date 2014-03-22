package com.regaltec.ida40.asip.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

/**
 * <p>综调系统和接口平台消息基类</p>
 * <p>创建日期：2010-9-3 下午01:58:59</p>
 *
 * @author 封加华
 */
public abstract class Message {
    protected Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * 发送方的系统编码
     */
    protected String sender="";
    /**
     * 接收方的系统码
     */
    protected String receiver="";
    /**
     * 调用时间
     */
    protected String time="";
    /**
     * 服务名称
     */
    protected String serviceName="";
    /**
     *  消息类型（request请求、response响应）
     */
    protected String msgType="";
    /**
     * 消息唯一标识，请求与响应消息的msg_id相同
     */
    protected String msgId="";
    /**
     * 私有数据定义
     */
    protected String keyword="";
    /**
     * 消息唯一标识，请求与响应消息的msg_id相同
     */
    protected String dataInfo="";

    /**
     * 
     * 构造函数
     * @param sender sender
     * @param receiver receiver
     * @param time time
     * @param serviceName serviceName
     * @param msgType msgType
     * @param msgId msgId
     * @param dataInfo dataInfo
     */
    public Message(String sender, String receiver, String time, String serviceName, String msgType, String msgId,
            String dataInfo) {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.serviceName = serviceName;
        this.msgType = msgType;
        this.msgId = msgId;
        this.dataInfo = dataInfo;
    }

    /**
     * 
     * 构造函数
     */
    public Message() {
        super();
    }

    /**
     * <p>返回当前日期的yyyy-MM-dd HH:mm:ss样式</p>
     * @return 当前日期
     */
    protected static String getCurrentDate() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(new Date());
    }

    /**
     * <p>生成32位随机msg_id，其中只包含26个字母和10个数字</p>
     * @return 消息id
     */
    protected static String getRandomMsgId() {
        char[] chs =
                new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                        's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        String randomString = RandomStringUtils.random(32, 0, chs.length, true, true, chs);
        return randomString;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
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
     * @return the dataInfo
     */
    public String getDataInfo() {
        return dataInfo;
    }

    /**
     * @param dataInfo the dataInfo to set
     */
    public void setDataInfo(String dataInfo) {
        this.dataInfo = dataInfo;
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
}
