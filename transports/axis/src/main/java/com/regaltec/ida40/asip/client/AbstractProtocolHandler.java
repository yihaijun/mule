/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.ida40.asip.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-4-7 上午09:22:12</p>
 *
 * @author 朱志欧
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {
    
    public static final String SOAP11_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    public static final String ASIP_NS = "http://service.asip.regaltec.com/";

    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * {@inheritDoc}
     */
    abstract public ResponseMessage execute(RequestContext context);

    /**
     * <p>解析接口平台soap响应消息</p>
     * @param soapResponse 响应串
     * @param context 请求上下文
     * @return 响应消息
     */
    protected ResponseMessage parseSoapResponse(String soapResponse, RequestContext context) {
        if (StringUtils.isBlank(soapResponse)) {
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0031", "调用web服务返回的消息体为空。", context);
        }
        String requestId = context.getRequestId();
        String moduleName = context.getBusinessModuleName();
        String serviceName = context.getRequestMessage().getServiceName();
        // 解析soap响应
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(soapResponse);
        } catch (DocumentException e) {
            logger.error(String.format("模块名称：%s, 服务名称： %s, 消息标识：%s, 无法解析WEB服务返回的消息体：", moduleName, serviceName, requestId), e);
            String faultString = "无法解析web服务返回的消息体(非xml或者xml不正确)，请检查调用的地址是否只提供http服务而不是web服务。";
            return AsipClient.buildExceptivelyResponseMessage("ASIP-0032", faultString, context);
        }
        Map<String, String> xmlMap = new HashMap<String, String>();
        xmlMap.put("soap", SOAP11_NS);
        xmlMap.put("asip", ASIP_NS);
        XPath x = doc.createXPath("/soap:Envelope/soap:Body/asip:callResponse/return");
        x.setNamespaceURIs(xmlMap);
        Element reqElement = (Element) x.selectSingleNode(doc);
        // 如果找不到需要的数据节点，则查找soap中的Fault标记，提取detail信息构建响应消息包
        if (reqElement != null) {
            try {// 正常情况
                return ResponseMessage.newInstance(reqElement.getText());
            } catch (ResponseMessageException e) {
                logger.error(String.format("模块名称：%s, 服务名称： %s, 消息标识：%s, 无法构建ResponseMessage消息：", moduleName, serviceName, requestId), e);
                //logger.error(String.format("接口平台ASIP异常,接口服务返回的xml文档解析时出错，以下是异常堆栈信息：", context.getRequestId()), e);
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0032", "接口平台异常,接口服务返回的xml文档解析时出错，以下是异常堆栈信息：\n"
                        + e.getMessage(), context);
            }
        } else {
            logger.error("模块名称："+moduleName+" 服务名称： "+ serviceName+", 消息标识:" +requestId+"返回的SOAP消息有异常，下面是原始SOAP消息内容，供开发人员核对:"+ soapResponse+"\n");
            x = doc.createXPath("/soap:Envelope/soap:Body/soap:Fault");
            x.setNamespaceURIs(xmlMap);
            Element faultElement = (Element) x.selectSingleNode(doc);
            if (faultElement != null) {
                StringBuffer errorInfo = new StringBuffer();
                x = doc.createXPath("/soap:Envelope/soap:Body/soap:Fault/faultcode");
                x.setNamespaceURIs(xmlMap);
                Element faultcodeElement = (Element) x.selectSingleNode(doc);
                errorInfo
                        .append("faultcode=" + (faultcodeElement != null ? faultcodeElement.getText() : "null") + "\n");
                x = doc.createXPath("/soap:Envelope/soap:Body/soap:Fault/faultstring");
                x.setNamespaceURIs(xmlMap);
                Element faultstringElement = (Element) x.selectSingleNode(doc);
                errorInfo.append("faultstring=" + (faultstringElement != null ? faultstringElement.getText() : "null")
                        + "\n");
                x = doc.createXPath("/soap:Envelope/soap:Body/soap:Fault/faultactor");
                x.setNamespaceURIs(xmlMap);
                Element faultactorElement = (Element) x.selectSingleNode(doc);
                errorInfo.append("faultactor=" + (faultactorElement != null ? faultactorElement.getText() : "null")
                        + "\n");
                x = doc.createXPath("/soap:Envelope/soap:Body/soap:Fault/detail");
                x.setNamespaceURIs(xmlMap);
                Element detailElement = (Element) x.selectSingleNode(doc);
                errorInfo.append("detail=" + (detailElement != null ? detailElement.getText() : "null"));
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0032",
                        "接口平台异常,以下是详细信息：\n" + errorInfo.toString() + "\n原始SOAP消息内容：\n" + soapResponse, context);
            } else {
                return AsipClient.buildExceptivelyResponseMessage("ASIP-0032", "接口平台异常,返回消息不是SOAP消息。", context);
            }
        }
    }
}
