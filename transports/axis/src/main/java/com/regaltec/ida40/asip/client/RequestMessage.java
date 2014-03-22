package com.regaltec.ida40.asip.client;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.apache.log4j.Logger;

/**
 * <p>接口平台请求消息</p>
 * <p>创建日期：2010-9-3 上午11:17:19</p>
 *
 * @author 封加华
 */
public class RequestMessage extends Message {
    private boolean simulateFlag = false;

    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接受者
     * @param time 请求时间
     * @param serviceName 服务名
     * @param msgId 消息id
     * @param dataInfo 业务数据
     */
    public RequestMessage(String sender, String receiver, 
            String time, String serviceName, String msgId, String dataInfo) {
        super(sender, receiver, time, serviceName, "request", msgId, dataInfo);
    }

    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接受者
     * @param serviceName 服务名
     * @param msgId 消息id
     * @param dataInfo 业务数据
     */
    public RequestMessage(String sender, String receiver, String serviceName, String msgId, String dataInfo) {
        this(sender, receiver, getCurrentDate(), serviceName, msgId, dataInfo);
    }

    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接受者
     * @param serviceName 服务名
     * @param dataInfo 业务数据
     */
    public RequestMessage(String sender, String receiver, String serviceName, String dataInfo) {
        this(sender, receiver, serviceName, getRandomMsgId(), dataInfo);
    }

    /**
     * 
     * 构造函数
     * @param receiver 接受者
     * @param serviceName 服务名
     * @param dataInfo 业务数据
     */
    public RequestMessage(String receiver, String serviceName, String dataInfo) {
        this("ida40", receiver, serviceName, dataInfo);
    }

    /**
     * 
     * 构造函数
     * @param serviceName 服务名
     * @param dataInfo 业务数据
     */
    public RequestMessage(String serviceName, String dataInfo) {
        this("asip", serviceName, dataInfo);
    }

    /**
     * 
     * 构造函数
     */
    public RequestMessage() {
        super();
    }

    /**
     * @return the simulateFlag
     */
    public boolean isSimulateFlag() {
        return simulateFlag;
    }

    /**
     * @param simulateFlag the simulateFlag to set
     */
    public void setSimulateFlag(boolean simulateFlag) {
        this.simulateFlag = simulateFlag;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        buf.append("<service>\r\n");
        buf.append("\t<head>\r\n");
        buf.append("\t\t<sender>")
                .append(StringEscapeUtils.escapeXml(this.getSender() != null ? this.getSender() : "")).append(
                        "</sender>\r\n");
        buf.append("\t\t<receiver>").append(
                StringEscapeUtils.escapeXml(this.getReceiver() != null ? this.getReceiver() : "")).append(
                "</receiver>\r\n");
        buf.append("\t\t<time>").append(StringEscapeUtils.escapeXml(this.getTime() != null ? this.getTime() : ""))
                .append("</time>\r\n");
        buf.append("\t\t<service_name>").append(
                StringEscapeUtils.escapeXml(this.getServiceName() != null ? this.getServiceName() : "")).append(
                "</service_name>\r\n");
        buf.append("\t\t<msg_type>").append("request").append("</msg_type>\r\n");
        buf.append("\t\t<msg_id>").append(StringEscapeUtils.escapeXml(this.getMsgId() != null ? this.getMsgId() : ""))
                .append("</msg_id>\r\n");
        buf.append("\t\t<simulate_flag>").append(isSimulateFlag()).append("</simulate_flag>\r\n");
        buf.append("\t\t<keyword>").append(getKeyword()).append("</keyword>\r\n");
        buf.append("\t</head>\r\n");
        buf.append("\t<data_info>\r\n").append((this.getDataInfo() != null ? this.getDataInfo() : "")).append(
                "\r\n\t</data_info>\r\n");
        buf.append("</service>");
        
        char[] tmpCharArry = buf.toString().toCharArray();
        for(int i = 0;i<tmpCharArry.length;i++){
            if(tmpCharArry[i] < '\t'){
                logger.error("系统有问题也,有不可见字符输入:["+(int)tmpCharArry[i]+"]i="+i);
                tmpCharArry[i]=' ';
            }
        }
        return  new String(tmpCharArry);
    }

    /**
     * 
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param xml xml
     * @return 请求消息对象
     * @throws AsipClientException 客户端异常
     */
    @SuppressWarnings("unchecked")
    public static RequestMessage newInstance(String xml) {
        RequestMessage message = null;
        if (isEmpty(xml)) {
            throw new IllegalArgumentException("xml不能为空");
        }
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element service = doc.getRootElement();
            Element head = service.element("head");
            if (isNotEmpty(head)) {
                message = new RequestMessage();
                message.setSender(head.elementTextTrim("sender"));
                message.setReceiver(head.elementTextTrim("receiver"));
                message.setMsgType(head.elementTextTrim("msg_type"));
                message.setMsgId(head.elementTextTrim("msg_id"));
                message.setTime(head.elementTextTrim("time"));
                message.setSimulateFlag(Boolean.valueOf(head.elementTextTrim("simulate_flag")));
                message.setServiceName(head.elementTextTrim("service_name"));
                message.setKeyword(head.elementTextTrim("keyword"));
                org.dom4j.Element dataInfo = service.element("data_info");
                if (isNotEmpty(dataInfo)) {
                    StringBuffer dataInfoChildNode = new StringBuffer();
                    List<Element> childNodes = dataInfo.elements();
                    for (Element e : childNodes) {
                        dataInfoChildNode.append(e.asXML());
                    }
                    message.setDataInfo(dataInfoChildNode.toString());
                }
            } else {
                throw new RuntimeException("xml格式不符合要求，没有发现head元素,原始内容：" + xml);
            }
        } catch (DocumentException e) {
            throw new RuntimeException("原始内容：" + xml, e);
        }
        return message;
    }

    /**
     * 判断<b>对象</b>和<b>内容</b>是否为空，参数可以传任意类型。
     * @param obj 任意具有空特征的对象
     * @return 返回boolean标识
     */
    @SuppressWarnings("unchecked")
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return ((String) obj).equals("");
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else {
            return false;
        }
    }
    /**
     * 
     *  判断<b>对象</b>和<b>内容</b>是否不为空，参数可以传任意类型。
     * @param obj 任意具有空特征的对象
     * @return 返回boolean标识
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
