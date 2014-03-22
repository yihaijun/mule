/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.transformer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.util.XMLUtils;
import org.mule.transformer.AbstractMessageTransformer;
import org.w3c.dom.Node;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-5-24 下午03:31:12</p>
 *
 * @author yihaijun
 */
// <transformer ref="ObjectToString"/>
//
// <expression-transformer>
// <return-argument evaluator="groovy" expression="payload" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_SENDER" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_RECEIVER" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_SERVICE_NAME" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_MSG_TYPE" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_MSG_ID" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_AB" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_BC" />
// <return-argument evaluator="header" expression="session:IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_CD" />
// </expression-transformer>
// <!--
// <expression-transformer> <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_SENDER',payload[1])"
// /> <return-argument evaluator="header"
// expression="session:IDA40_ASIP_MSG_HEAD_RECEIVER" />
// </expression-transformer> <expression-transformer>
// <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_RECEIVER',payload[1])"
// /> <return-argument evaluator="header"
// expression="session:IDA40_ASIP_MSG_HEAD_SERVICE_NAME" />
// </expression-transformer> <expression-transformer>
// <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_SERVICE_NAME',payload[1])"
// /> <return-argument evaluator="header"
// expression="session:IDA40_ASIP_MSG_HEAD_MSG_TYPE" />
// </expression-transformer> <expression-transformer>
// <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_MSG_TYPE',payload[1])"
// /> <return-argument evaluator="header"
// expression="session:IDA40_ASIP_MSG_HEAD_MSG_ID" />
// </expression-transformer> <expression-transformer>
// <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_MSG_ID',payload[1])"
// /> <return-argument evaluator="header"
// expression="session:IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE" />
// </expression-transformer> <expression-transformer>
// <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE',payload[1])"
// /> <return-argument evaluator="header"
// expression="session:IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO" />
// </expression-transformer> <expression-transformer>
// <return-argument evaluator="groovy"
// expression="org.mule.util.StringUtils.replace(payload[0],'IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO',payload[1])"
// /> </expression-transformer>
// -->
// <script:transformer>
// <script:script engine="groovy">
// <![CDATA[
// import org.mule.util.StringUtils
//
// def xml = payload[0]
// def time_consuming_ab = "\r\n\t\t<time_consuming_ab>" + payload[9] + "</time_consuming_ab>"
// def time_consuming_bc = "\r\n\t\t<time_consuming_bc>" + payload[10] + "</time_consuming_bc>"
// def time_consuming_cd = "\r\n\t\t<time_consuming_cd>" + payload[11] + "</time_consuming_cd>"
//
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_SENDER",payload[1])
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_RECEIVER",payload[2])
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_SERVICE_NAME",payload[3])
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_MSG_TYPE",payload[4])
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_MSG_ID",payload[5])
//
// xml =
// StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG</simulate_flag>",payload[8]+"</simulate_flag>"+time_consuming_ab+time_consuming_bc+time_consuming_cd)
//
// if(StringUtils.contains(xml,"<error_code>ASIP-2050</error_code>")){
// errorInf=StringUtils.substringBetween(xml, "<error_info>", "</error_info>")
// if(errorInf== null){
// errorInf = ""
// }
// message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", "ASIP-2050")
// message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", errorInf)
// }else if(!StringUtils.contains(xml,"<error_code>IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE</error_code>") &&
// !StringUtils.contains(xml,"<error_code>ASIP-0000</error_code>")){
// errorCode=StringUtils.substringBetween(xml, "<error_code>", "</error_code>")
// errorInf=StringUtils.substringBetween(xml, "<error_info>", "</error_info>")
// if(errorCode==null){
// errorCode = "ASIP-0000"
// }
// if(errorInf== null){
// errorInf = ""
// }
// message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", errorCode)
// message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", errorInf)
// }
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE",payload[6])
// xml = StringUtils.replace(xml,"IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO",payload[7])
//
// result = xml
// ]]>
// </script:script>
// </script:transformer>

public class ObjectToAsipAsipServiceReturnTransformer extends AbstractMessageTransformer {
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        String result = "";
        /**
         * 发送方的系统编码
         */
        String sender = "";
        /**
         * 接收方的系统码
         */
        String receiver = "";
        /**
         * 调用时间
         */
        String time = "";
        /**
         * 服务名称
         */
        String serviceName = "";
        /**
         *  消息类型（request请求、response响应）
         */
        String msgType = "";
        /**
         * 消息唯一标识，请求与响应消息的msg_id相同
         */
        String msgId = "";
        /**
         * 接口组名称
         */
        String appName = "";
        /**
         * 错误代码
         */
        String errorCode = "";
        /**
         * 错误描述
         */
        String errorInfo = "";
        /**
         * 是否摸拟测试消息
         */
        String simulateFlag = "";

        String consumingAb = "";
        String consumingBc = "";
        String consumingCd = "";
        
        String keyword = "";
        
        result = message.getPayload().toString();
        sender = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SENDER");
        receiver = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_RECEIVER");
        serviceName = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SERVICE_NAME");
        msgType = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_TYPE");
        msgId = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ID");
        errorCode = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE");
        errorInfo = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO");
        simulateFlag = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG");
        consumingAb = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_AB");
        consumingBc = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_BC");
        consumingCd = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_TIME_CONSUMING_CD");
        keyword = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_KEYWORD");

        StringBuffer time_consuming = new StringBuffer();
        time_consuming.delete(0, time_consuming.length());

        time_consuming.append("\r\n\t\t<time_consuming_ab>");
        time_consuming.append(consumingAb);
        time_consuming.append("</time_consuming_ab>");
        time_consuming.append("\r\n\t\t<time_consuming_bc>");
        time_consuming.append(consumingBc);
        time_consuming.append("</time_consuming_bc>");
        time_consuming.append("\r\n\t\t<time_consuming_cd>");
        time_consuming.append(consumingCd);
        time_consuming.append("</time_consuming_cd>");
        time_consuming.append("</head>");
        result = org.mule.util.StringUtils.replace(result, "</head>", time_consuming.toString());

        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG", simulateFlag);
        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_SENDER", sender);
        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_RECEIVER", receiver);
        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_SERVICE_NAME", serviceName);
        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_MSG_TYPE", msgType);
        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_MSG_ID", msgId);

        
        String newErrorCode = errorCode;
        String newErrorInfo = errorInfo;

        try {
            Node resultNode = (Node) XMLUtils.toW3cDocument(result);
            newErrorCode = XMLUtils.selectValue("/service/head/error_code", resultNode);
            if (!newErrorCode.equalsIgnoreCase("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE")
                    && !newErrorCode.equalsIgnoreCase("ASIP-0000")) {
                if(newErrorCode.length()>10){
                    newErrorCode = newErrorCode.substring(0, 10);
                }
                newErrorInfo = XMLUtils.selectValue("/service/head/error_info", resultNode);
                if(newErrorInfo.length()>255){
                    newErrorInfo = newErrorInfo.substring(0, 255);
                }
                message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", newErrorCode);
                message.setSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", newErrorInfo);
            } else {
                newErrorCode = errorCode;
            }
            String newKeyWord = XMLUtils.selectValue("/service/head/keyword", resultNode);
            if (!newKeyWord.equalsIgnoreCase("IDA40_ASIP_MSG_HEAD_KEYWORD")
                    && !newKeyWord.equalsIgnoreCase("")) {
                message.setSessionProperty("IDA40_ASIP_MSG_HEAD_KEYWORD", newKeyWord);
            }
        } catch (Exception e) {
        }

        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_MSG_ERROR_CODE", newErrorCode);
        result = org.mule.util.StringUtils.replace(result, "IDA40_ASIP_MSG_HEAD_MSG_ERROR_INFO", newErrorInfo);

        return result;
    }

}
