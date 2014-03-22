/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.service;

import java.util.Map;
import org.mule.module.xml.util.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-12-27 下午07:18:56</p>
 *
 * @author yihaijun
 */
public class AsigServiceParam extends AsipServiceParam {

    /**
     * 
     */
    private static final long serialVersionUID = -5446796552297930890L;

    private String functionCode = "";
    private String paramDocCommandId = "";
    private String asigServiceName = "";

    public boolean parserBusinessServiceParam(String param) {
        super.parserBusinessServiceParam(param);
        try {
            Node node = getParamNode();
            functionCode = XMLUtils.selectValue("/root/functionCode", node);
            paramDocCommandId = XMLUtils.selectValue("/root/paramDoc/commandId", node);
            asigServiceName = fetchAsigServiceName(node);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> createMuleMessageProperties() {
        Map<String, Object> messageProperties = super.createMuleMessageProperties();
        messageProperties.put("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", getCxf_operation());
        return messageProperties;
    }

    
    
    /**
     *     
     * <p>功能的简单描述，参数、返回值及异常必须注明。</p>
     * @param node
     * @return
     * 以下代码经测试验证是错误的:
       if(node.getFirstChild().getNodeName().equals("SERVICE")&&node.getFirstChild().getChildNodes().getLength()==1){
            asigServiceName=node.getFirstChild().getFirstChild().getNodeName();
        }
     * 
     */
    private String fetchAsigServiceName(Node node) {
        if (!node.getFirstChild().getNodeName().equals("SERVICE")) {
            return "";
        }
        NodeList nodeList = node.getFirstChild().getChildNodes();
        int len = nodeList.getLength();
        Node childNode = null;
        for (int i = 0; i < len; i++) {
            childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            asigServiceName = childNode.getNodeName();
            if(asigServiceName.equals("DEBUG")){
                super.setSimulateFlag("DEBUG");
            }
            return asigServiceName;
        }
        return "";
    }

    /**
     * @return the functionCode
     */
    public String getFunctionCode() {
        return functionCode;
    }

    /**
     * @return the paramDocCommandId
     */
    public String getParamDocCommandId() {
        return paramDocCommandId;
    }

    /**
     * @return the asigServiceName
     */
    public String getAsigServiceName() {
        return asigServiceName;
    }

    public String toString() {
        return super.toString();
    }
}
