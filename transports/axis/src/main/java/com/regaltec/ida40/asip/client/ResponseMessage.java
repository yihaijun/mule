package com.regaltec.ida40.asip.client;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.util.HtmlUtils;

//import com.regaltec.ida40.utils.ObjectUtils;

/**
 * <p>接口平台响应消息</p>
 * <p>创建日期：2010-9-3 下午02:00:05</p>
 *
 * @author 封加华
 */
public class ResponseMessage extends Message {
    
    private static Logger logger = Logger.getLogger(ResponseMessage.class.getName());
    private String content="";
    
    /**
     * 错误代码
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorInfo;

    private long timeConsumingAB = 0; 
    private long timeConsumingBC = 0; 
    private long timeConsumingCD = 0; 
    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接收者
     * @param time 时间
     * @param serviceName 服务名
     * @param msgType 消息类型
     * @param msgId 消息流水id
     * @param dataInfo 数据
     * @param errorCode 错误码
     * @param errorInfo 错误描述
     */
    public ResponseMessage(String sender, String receiver, String time, String serviceName, String msgType,
            String msgId, String dataInfo, String errorCode, String errorInfo) {
        super(sender, receiver, time, serviceName, msgType, msgId, dataInfo);
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }

    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接收者
     * @param serviceName 服务名
     * @param msgId 消息id
     * @param dataInfo 数据
     * @param errorCode 错误码
     * @param errorInfo 错误描述
     */
    public ResponseMessage(String sender, String receiver, String serviceName, String msgId, String dataInfo,
            String errorCode, String errorInfo) {
        this(sender, receiver, getCurrentDate(), serviceName, "response", msgId, dataInfo, errorCode, errorInfo);
    }

    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接收者
     * @param serviceName 服务名
     * @param msgId 消息id
     * @param dataInfo 数据
     */
    public ResponseMessage(String sender, String receiver, String serviceName, String msgId, String dataInfo) {
        this(sender, receiver, getCurrentDate(), serviceName, "response", msgId, dataInfo, null, null);
    }

    /**
     * 
     * 构造函数
     * @param sender 发送者
     * @param receiver 接收者
     * @param serviceName 服务名
     * @param dataInfo 数据
     */
    public ResponseMessage(String sender, String receiver, String serviceName, String dataInfo) {
        this(sender, receiver, serviceName, getRandomMsgId(), dataInfo);
    }

    /**
     * 
     * 构造函数
     * @param receiver 发送者
     * @param serviceName 服务名 
     * @param dataInfo 数据
     */
    public ResponseMessage(String receiver, String serviceName, String dataInfo) {
        this("asip", receiver, serviceName, dataInfo);
    }

    /**
     * 
     * 构造函数
     * @param serviceName 服务名
     * @param dataInfo 数据
     */
    public ResponseMessage(String serviceName, String dataInfo) {
        this("ida40", serviceName, dataInfo);
    }

    /**
     * 
     * 构造函数
     */
    public ResponseMessage() {
        super();
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
     * @return the timeConsumingAB
     */
    public long getTimeConsumingAB() {
        return timeConsumingAB;
    }

    /**
     * @param timeConsumingAB the timeConsumingAB to set
     */
    private void setTimeConsumingAB(long timeConsumingAB) {
        this.timeConsumingAB = timeConsumingAB;
    }

    /**
     * @return the timeConsumingBC
     */
    public long getTimeConsumingBC() {
        return timeConsumingBC;
    }

    /**
     * @param timeConsumingBC the timeConsumingBC to set
     */
    private void setTimeConsumingBC(long timeConsumingBC) {
        this.timeConsumingBC = timeConsumingBC;
    }

    /**
     * @return the timeConsumingCD
     */
    public long getTimeConsumingCD() {
        return timeConsumingCD;
    }

    /**
     * @param timeConsumingCD the timeConsumingCD to set
     */
    private void setTimeConsumingCD(long timeConsumingCD) {
        this.timeConsumingCD = timeConsumingCD;
    }

    /**
     * <p>判断响应是否存在错误</p>
     * <p>各业务调用可以使用类似如下代码方式进行工作</p>
     * <code>
     * ResponseMessage rm = new AsipClient(ip,port,uri).call("predeal",requestMessage,3000,50000);
     * if(!rm.isFault()){
     *     //TODO put your code here
     * }else{
     *    throw new BOException(rm.getErrorInfo);
     * }
     * </code>
     * @return 返回true，表示接口调用不正常；返回false，表示接口调用正常。
     */
    public boolean isFault() {
        return !"ASIP-0000".equals(this.getErrorCode());
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String toString() {
        if(content!=null && !content.equals("")){
            return content;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        buf.append("<service>\r\n");
        buf.append("\t<head>\r\n");
        buf.append("\t\t<sender>");
        buf.append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(this.getSender())));
        buf.append("</sender>\r\n");
        buf.append("\t\t<receiver>");
        buf.append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(this.getReceiver())));
        buf.append("</receiver>\r\n");
        buf.append("\t\t<time>");
        buf.append(StringEscapeUtils.escapeXml(StringUtils.trimToEmpty(this.getTime())));
        buf.append("</time>\r\n");
        buf.append("\t\t<service_name>");
        buf.append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(this.getServiceName())));
        buf.append("</service_name>\r\n");
        buf.append("\t\t<msg_type>response</msg_type>\r\n");
        buf.append("\t\t<msg_id>");
        buf.append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(this.getMsgId())));
        buf.append("</msg_id>\r\n");
        buf.append("\t\t<error_code>");
        buf.append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(this.getErrorCode())));
        buf.append("</error_code>\r\n");
        buf.append("\t\t<error_info>");
        buf.append(HtmlUtils.htmlEscape(StringUtils.trimToEmpty(this.getErrorInfo())));
        buf.append("</error_info>\r\n");
        buf.append("\t</head>\r\n");
        buf.append("\t<data_info>\r\n").append((this.getDataInfo() != null ? this.getDataInfo() : "")).append(
                "\t</data_info>\r\n");
        buf.append("</service>");
        return buf.toString();
    }

    /**
     * 
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param xml xml
     * @return 响应消息对象 
     * @throws AsipClientException 
     */
    @SuppressWarnings("unchecked")
    public static ResponseMessage newInstance(String xml) throws ResponseMessageException {
        ResponseMessage message = null;
        if (StringUtils.isBlank(xml)) {
            throw new IllegalArgumentException("接口响应xml串不能为空");
        }
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element service = doc.getRootElement();
            Element head = service.element("head");
            if (isNotEmpty(head)) {
                message = new ResponseMessage();
                message.content= xml;
                message.setSender(head.elementTextTrim("sender"));
                message.setReceiver(head.elementTextTrim("receiver"));
                message.setMsgType(head.elementTextTrim("msg_type"));
                message.setMsgId(head.elementTextTrim("msg_id"));
                message.setTime(head.elementTextTrim("time"));
                message.setServiceName(head.elementTextTrim("service_name"));
                message.setErrorCode(head.elementTextTrim("error_code"));
                message.setErrorInfo(head.elementTextTrim("error_info"));
                String time_consuming_ab = head.elementTextTrim("time_consuming_ab");
                String time_consuming_bc = head.elementTextTrim("time_consuming_bc");
                String time_consuming_cd = head.elementTextTrim("time_consuming_cd");
                if(time_consuming_ab!= null && !time_consuming_ab.equals("")){
                    message.setTimeConsumingAB(Integer.parseInt(time_consuming_ab));
                }
                if(time_consuming_ab!= null && !time_consuming_ab.equals("")){
                    message.setTimeConsumingBC(Integer.parseInt(time_consuming_bc));
                }
                if(time_consuming_ab!= null && !time_consuming_ab.equals("")){
                    message.setTimeConsumingCD(Integer.parseInt(time_consuming_cd));
                }
                Element dataInfo = service.element("data_info");
                if (isNotEmpty(dataInfo)) {
                    StringBuffer dataInfoChildNode = new StringBuffer();
                    List<Element> childNodes = dataInfo.elements();
                    for (Element e : childNodes) {
                        dataInfoChildNode.append(e.asXML());
                    }
                    message.setDataInfo(dataInfoChildNode.toString());
                }
            } else {
                logger.error("ResponseMessage Exception:没有发现head元素,xml=[" + xml + "]");
                throw new ResponseMessageException("接口响应xml串格式不符合规定，没有发现head元素，原始内容：" + xml);
            }
        } catch (DocumentException e) {
            String encode = System.getProperty("file.encoding");
            //logger.error(String.format("ResponseMessage Exception: 解析接口响应xml串出错,当前系统编码是%s,xml=[%s]", encode, xml), e);
            throw new ResponseMessageException("解析服务响应出错,当前系统编码是" + encode + ",服务响应是:[" + xml + "]", e);
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
