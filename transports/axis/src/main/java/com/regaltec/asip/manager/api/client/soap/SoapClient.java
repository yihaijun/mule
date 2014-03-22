package com.regaltec.asip.manager.api.client.soap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;

/**
 * 
 * <p>访问webservice。</p>
 * <p>创建日期：2010-12-2 下午05:46:28</p>
 *
 * @author yihaijun
 */
@SuppressWarnings("unchecked")
public class SoapClient {
    private String userName = "";
    private String password = "";
    private int timeout = -1;
    private int connectTimeout = -1;
    private String serviceName = "";
    private String portName = "";
    private String url = "";

    public SoapClient() {
    }

    public int initiate(String url, String strUserName, String strPassword) {
            return initiate(url, strUserName, strPassword, timeout);
    }

    public int initiate(String url, String strUserName, String strPassword, String strServiceName, String strPortName) {
            return initiate(url, strUserName, strPassword, strServiceName, strPortName, timeout,connectTimeout);
    }

    public int initiate(String url, String strUserName, String strPassword, int timeout) {
            return initiate(url, strUserName, strPassword, "", "", timeout,connectTimeout);
    }

    public int initiate(String url, String strUserName, String strPassword, int timeout,int connectTimeout) {
        return initiate(url, strUserName, strPassword, "", "", timeout,connectTimeout);
}
 
    public int CallFunction(String functionName, List paramList, StringBuffer strRetString) {
            return callFunction(functionName, paramList, strRetString);
    }

    public int initiate(String url, String strUserName, String strPassword, String strServiceName, String strPortName, int timeout,int connectTimeout) {
            this.url = url;
            this.userName = strUserName;
            this.password = strPassword;
            this.serviceName = strServiceName;
            this.portName = strPortName;
            this.timeout = timeout;
            if(connectTimeout<=0){
                this.connectTimeout = this.timeout; 
            }else{
                this.connectTimeout = connectTimeout;
            }
            return 0;
    }

    public java.lang.String callFunFor1StringParam(java.lang.String functionName, java.lang.String inXmlString) // throws
    // java.rmi.RemoteException
    {
            List paramList = new ArrayList();
            HashMap map = new HashMap();
            map.put("name", "inputXml");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", stripNonValidXMLCharacters(inXmlString));                    //Edit by yihaijun at 2009.11.09,add stripNonValidXMLCharacters method  
            paramList.add(map);
            StringBuffer buf = new StringBuffer();
            this.callFunction(functionName, (java.util.List) paramList, buf);
            return buf.toString();
    }

    /**
 * This method ensures that the output String has only
 * valid XML unicode characters as specified by the
 * XML 1.0 standard. For reference, please see
 * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
 * standard</a>. This method will return an empty
 * String if the input is null or empty.
 *
 * @param in The String whose non-valid characters we want to remove.
 * @return The in String, stripped of non-valid characters.
 */
private String stripNonValidXMLCharacters(String in) {
    StringBuffer out = new StringBuffer(); // Used to hold the output.
    char current; // Used to reference the current character.

    if (in == null || ("".equals(in))) return ""; // vacancy test.
    for (int i = 0; i < in.length(); i++) {
        current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
        if ((current == 0x9) ||
            (current == 0xA) ||
            (current == 0xD) ||
            ((current >= 0x20) && (current <= 0xD7FF)) ||
            ((current >= 0xE000) && (current <= 0xFFFD)) ||
            ((current >= 0x10000) && (current <= 0x10FFFF))){
            out.append(current);
        }else{
                    Logger log = Logger.getLogger("regaltec.asig.soap.SoapClient");
                    log.warn("stripNonValidXMLCharacters:0x"+Integer.toHexString(current));
        }
    }
    return out.toString();
}    
    
    public java.lang.String callFunFor2StringParam(java.lang.String functionName, java.lang.String strPa1, java.lang.String strPa2) // throws
    // java.rmi.RemoteException
    {
            List paramList = new ArrayList();
            HashMap map = new HashMap();
            map.put("name", "strPa1");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa1);
            paramList.add(map);
            map = new HashMap();
            map.put("name", "strPa2");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa2);
            paramList.add(map);
            StringBuffer buf = new StringBuffer();
            this.callFunction(functionName, (java.util.List) paramList, buf);
            return buf.toString();
    }

    public java.lang.String callFunFor1IntParam(java.lang.String functionName, int iPa) // throws
    // java.rmi.RemoteException
    {
            List paramList = new ArrayList();
            HashMap map = new HashMap();
            map.put("name", "iPa");
            map.put("type", XMLType.SOAP_INT);
            map.put("value", new Integer(iPa));
            paramList.add(map);
            StringBuffer buf = new StringBuffer();
            this.callFunction(functionName, (java.util.List) paramList, buf);
            return buf.toString();
    }

    public java.lang.String callFunFor1String2IntParam(java.lang.String functionName, java.lang.String strPa1, int iPa1, int iPa2) // throws
    // java.rmi.RemoteException
    {
            List paramList = new ArrayList();
            HashMap map = new HashMap();
            map.put("name", "strPa1");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa1);
            paramList.add(map);
            map = new HashMap();
            map.put("name", "iPa1");
            map.put("type", XMLType.SOAP_INT);
            map.put("value", new Integer(iPa1));
            paramList.add(map);
            map = new HashMap();
            map.put("name", "iPa2");
            map.put("type", XMLType.SOAP_INT);
            map.put("value", new Integer(iPa2));
            paramList.add(map);
            StringBuffer buf = new StringBuffer();
            this.callFunction(functionName, (java.util.List) paramList, buf);
            return buf.toString();
    }

    public java.lang.String callFunFor2String2IntParam(java.lang.String functionName, java.lang.String strPa1, java.lang.String strPa2, int iPa1, int iPa2) // throws
    // java.rmi.RemoteException
    {
            List paramList = new ArrayList();
            HashMap map = new HashMap();
            map.put("name", "strPa1");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa1);
            paramList.add(map);
            map = new HashMap();
            map.put("name", "strPa2");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa2);
            paramList.add(map);
            map = new HashMap();
            map.put("name", "iPa1");
            map.put("type", XMLType.SOAP_INT);
            map.put("value", new Integer(iPa1));
            paramList.add(map);
            map = new HashMap();
            map.put("name", "iPa2");
            map.put("type", XMLType.SOAP_INT);
            map.put("value", new Integer(iPa2));
            paramList.add(map);
            StringBuffer buf = new StringBuffer();
            this.callFunction(functionName, (java.util.List) paramList, buf);
            return buf.toString();
    }

    public int callFunFor3StringParam(java.lang.String functionName, java.lang.String strPa1, java.lang.String strPa2, java.lang.String strPa3,StringBuffer buf) // throws
    // java.rmi.RemoteException
    {
            List paramList = new ArrayList();
            HashMap map = new HashMap();
            map.put("name", "strPa1");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa1);
            paramList.add(map);
            map = new HashMap();
            map.put("name", "strPa2");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa2);
            paramList.add(map);
            map = new HashMap();
            map.put("name", "strPa3");
            map.put("type", XMLType.SOAP_STRING);
            map.put("value", strPa3);
            paramList.add(map);
            buf.delete(0, buf.length());
            return this.callFunction(functionName, (java.util.List) paramList, buf);
    }

    public int callFunction(java.lang.String functionName, java.util.List paramList, StringBuffer strRetString) {
            return callFunction(url, userName, password, serviceName, portName, timeout, functionName, paramList, strRetString);
    }

    public int callFunction(java.lang.String url, java.lang.String strUserName, java.lang.String strPassword, java.lang.String strServiceName, java.lang.String strPortName, int timeout, java.lang.String functionName, java.util.List paramList, StringBuffer strRetString) {

            WebDynamicInvoker invoker = null;
            java.util.Vector parameterValues = null;
            java.util.Map outputs = null;
            java.util.Vector v = null;

            parameterValues = new java.util.Vector();
            org.apache.axis.wsdl.symbolTable.Parameters parameters;
            try {
                    invoker = new WebDynamicInvoker(url,connectTimeout);
            } catch (Exception e) {
                    strRetString.append("<WebServiceClientRemoteException>");
                    strRetString.append("[错误原因:可能是URL错误]");
                    strRetString.append("</WebServiceClientRemoteException>");
                    return 0;
            }
            java.lang.StringBuffer serviceNameBuf = new StringBuffer(strServiceName);
            java.lang.StringBuffer portNameBuf = new StringBuffer(strPortName);
            try {
                    parameters = invoker.enumParameters(serviceNameBuf.toString(), portNameBuf.toString(), functionName);
            } catch (Exception e) {
                    strRetString.append("<WebServiceClientRemoteException>");
                    strRetString.append("[错误原因:可能是serviceName或portName或functionName错误]");
                    strRetString.append("</WebServiceClientRemoteException>");
                    return 0;
            }
            try {
                    v = parameters.list;
                    int paraNumbers = v.size();

                    int i = 0;
                    while (i < paraNumbers) {
                            parameterValues.addElement(((Map) paramList.get(i)).get("value"));
                            i++;
                    }
                    outputs = invoker.invoke(serviceNameBuf.toString(), portNameBuf.toString(), functionName, parameterValues, timeout);
                    Iterator keyIt = outputs.keySet().iterator();
                    if (keyIt.hasNext()) {
                            String keyName = keyIt.next().toString();
                            strRetString.append(outputs.get(keyName).toString());
                    }
            } catch (Exception e) {
//                    e.printStackTrace();
                    strRetString.append("<WebServiceClientRemoteException>");
                    strRetString.append(org.apache.commons.lang.StringEscapeUtils.escapeXml(e.toString()));
                    if (invoker == null) {
                            strRetString.append("[错误原因:可能是URL错误]");
                    } else if (v == null) {
                            strRetString.append("[错误原因:可能是serviceName或portName或functionName错误]");
                    } else if (outputs == null) {
                            strRetString.append("[错误原因:可能是远端服务异常]");
                    }
                    strRetString.append("</WebServiceClientRemoteException>");
            }
            return 0;
    }
}