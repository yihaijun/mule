/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.routing.filter;

import java.util.Properties;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;

import com.regaltec.asip.service.AsipServiceParam;
import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-4-24 下午03:52:59</p>
 *
 * @author yihaijun
 */
public class AsipClientFilter implements Filter {
    private String clienPermissionsFactor = "";
    private Properties clienPermissionsFactorProperties = null;

    @Override
    public boolean accept(MuleMessage message) {
        clienPermissionsFactorProperties =
                new PropertiesMapping("asipconf/clienPermissionsFactor.properties").getProperties();
        try {
            if (clienPermissionsFactor == null || clienPermissionsFactor.equals("")) {
                return true;
            }
            String clientIp = "";
            String allowClientIps =
                    clienPermissionsFactorProperties.getProperty(
                            clienPermissionsFactor + ".clienPermissionsFactor.allow", "").trim();
            String unAllowClientIps =
                    clienPermissionsFactorProperties.getProperty(
                            clienPermissionsFactor + ".clienPermissionsFactor.unAllow", "").trim();

            if ((allowClientIps == null || allowClientIps.equals(""))
                    && (unAllowClientIps == null || unAllowClientIps.equals(""))) {
                return true;
            }
            if (message.getSessionProperty("ASIP_REMOTE_CLIENT_ADDRESS") != null) {
                clientIp = message.getSessionProperty("ASIP_REMOTE_CLIENT_ADDRESS");
            } else if (message.getOutboundProperty("ASIP_REMOTE_CLIENT_ADDRESS") != null) {
                clientIp = message.getOutboundProperty("ASIP_REMOTE_CLIENT_ADDRESS");
            } else {
                return returnFalseAndEditPayload(message, "AsipClientFilter no passed!because clientIp is null.");
            }

            clientIp = org.apache.commons.lang.StringUtils.substringBetween(clientIp, "/", ":");

            if ((!(allowClientIps == null || allowClientIps.equals(""))) && (!(matchIp(allowClientIps, clientIp)))) {
                return returnFalseAndEditPayload(message, "AsipClientFilter no passed!because matchIp(allowClientIps,"
                        + clientIp + ") false.");
            }

            if ((matchIp(unAllowClientIps, clientIp))) {
                return returnFalseAndEditPayload(message,
                        "AsipClientFilter no passed!because matchIp(unAllowClientIps," + clientIp + ") true.");
            }

            return true;
        } catch (Exception e) {
            return returnFalseAndEditPayload(message, "AsipClientFilter no passed!because Exception:" + e.toString());
        }
    }

    private boolean returnFalseAndEditPayload(MuleMessage message, String errInf) {
        errInf = errInf + "(Inbound = " + message.getInboundProperty("MULE_ENDPOINT", "");
        errInf = errInf + ",Outbound = " + message.getOutboundProperty("MULE_ENDPOINT", "") + ")";
        Object payload = message.getPayload();
        if (payload instanceof AsipServiceParam) {
            String oldBusinessServiceParam = ((AsipServiceParam) payload).getBusinessServiceParam();
            String newBusinessServiceParam = appErrInfForBusinessServiceParam(oldBusinessServiceParam, errInf);
            ((AsipServiceParam) payload).setBusinessServiceParam(newBusinessServiceParam);
        } else if (payload instanceof Object[]) {
            Object[] payloadArry = (Object[]) payload;
            if (payloadArry.length >= 2 && payloadArry[1] instanceof String) {
                String oldBusinessServiceParam = (String) payloadArry[1];
                String newBusinessServiceParam = appErrInfForBusinessServiceParam(oldBusinessServiceParam, errInf);
                payloadArry[1] = newBusinessServiceParam;
            } else {
                Object[] newPayloadArry = new Object[payloadArry.length + 1];
                for (int i = 0; i < payloadArry.length; i++) {
                    newPayloadArry[i] = payloadArry[i];
                }
                newPayloadArry[payloadArry.length] = appErrInfForBusinessServiceParam("", errInf);
                payload = newPayloadArry;
            }
        } else {
            payload = appErrInfForBusinessServiceParam("", errInf);
        }

        message.setPayload(payload);
        return false;
    }

    private String appErrInfForBusinessServiceParam(String oldBusinessServiceParam, String errInf) {
        StringBuffer newBusinessServiceParam = new StringBuffer();
        newBusinessServiceParam.delete(0, newBusinessServiceParam.length());

        errInf = errInf + "(clienPermissionsFactor=" + clienPermissionsFactor + ")";
        int pos = oldBusinessServiceParam.lastIndexOf("<");
        if (pos > 0) {
            newBusinessServiceParam.append(oldBusinessServiceParam.substring(0, pos));
            newBusinessServiceParam.append("\r\n<AsipClientFilter>");
            newBusinessServiceParam.append(errInf);
            newBusinessServiceParam.append("</AsipClientFilter>\r\n<");
            newBusinessServiceParam.append(oldBusinessServiceParam.substring(pos + 1));
        } else {
            newBusinessServiceParam.append("<root>\r\n<original>");
            newBusinessServiceParam.append(oldBusinessServiceParam);
            newBusinessServiceParam.append("</original>\r\n<AsipClientFilter>");
            newBusinessServiceParam.append(errInf);
            newBusinessServiceParam.append("</AsipClientFilter>\r\n</root>");
        }
        return newBusinessServiceParam.toString();
    }

    private boolean matchIp(String ips, String ip) {
        StringBuffer allIp = new StringBuffer();
        allIp.delete(0, allIp.length());
        String[] ipsSectionSet = ips.split(";");
        for (int i = 0; i < ipsSectionSet.length; i++) {
            paraseIpsSection(ipsSectionSet[i], allIp);
        }
        if (allIp.indexOf("[0.0.0.0]") >= 0 || allIp.indexOf("[" + ip + "]") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private void paraseIpsSection(String ipsSection, StringBuffer allIP) {
        if (ipsSection.indexOf("-") < 0) {
            allIP.append("[");
            allIP.append(ipsSection);
            allIP.append("]");
            return;
        }
        String firstIp = ipsSection.split("-")[0];
        String lastIpTail = ipsSection.split("-")[1];
        String[] prefixIpSet = firstIp.split("\\.");
        String firstIpTail = prefixIpSet[3];
        String prefixIp = prefixIpSet[0] + "." + prefixIpSet[1] + "." + prefixIpSet[2] + ".";

        int beginIpTail = Integer.parseInt(firstIpTail);
        int endIpTail = Integer.parseInt(lastIpTail);
        if (endIpTail < beginIpTail) {
            int tmpIpTail = endIpTail;
            endIpTail = beginIpTail;
            beginIpTail = tmpIpTail;
        }

        for (int pos = beginIpTail; pos <= endIpTail; pos++) {
            allIP.append("[");
            allIP.append(prefixIp + pos);
            allIP.append("]");
        }
    }

    /**
     * @return the clienPermissionsFactor
     */
    public String getClienPermissionsFactor() {
        return clienPermissionsFactor;
    }

    /**
     * @param clienPermissionsFactor the clienPermissionsFactor to set
     */
    public void setClienPermissionsFactor(String clienPermissionsFactor) {
        this.clienPermissionsFactor = clienPermissionsFactor;
    }
}
