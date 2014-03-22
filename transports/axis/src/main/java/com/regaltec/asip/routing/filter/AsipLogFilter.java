/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.routing.filter;

import java.util.Hashtable;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;

import com.regaltec.asip.utils.IdaMsgUtils;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-2-12 上午09:02:31</p>
 *
 * @author yihaijun
 */
public class AsipLogFilter implements Filter {
    @SuppressWarnings("unchecked")
    private static Hashtable servicesFilete = new Hashtable();
    private static boolean filterAll = false;

    public static void setFilterAll() {
        filterAll = true;
    }

    @SuppressWarnings("unchecked")
    public static void addFilter(String filter) {
        servicesFilete.put(filter, "true");
    }

    public static void removeFilter(String filter) {
        servicesFilete.remove(filter);
    }

    @SuppressWarnings("unchecked")
    public static void removeAllFilter() {
        filterAll = false;
        servicesFilete.clear();
        servicesFilete = new Hashtable();
    }

    public static boolean accept(String appName, String serviceName, String clientIP, String simulateFlag) {
        return accept(appName, serviceName, clientIP, simulateFlag, "");
    }

    public static boolean accept(String appName, String serviceName, String clientIP, String simulateFlag,
            String msgType) {
        if (filterAll) {
            return false;
        }
        if (!accept(clientIP, simulateFlag)) {
            return false;
        }
        boolean accept = accept(appName);
        if (accept) {
            accept = accept(appName + "." + serviceName);
            if (accept) {
                accept = accept("." + serviceName);
            }
        }

        int msgTypeCode = IdaMsgUtils.getMsgType(msgType);
        if (accept && msgType != null && !msgType.equals("") && msgTypeCode > 0 && msgTypeCode < 5) {
            com.regaltec.asip.utils.PropertiesMapping conf =
                    new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");

            String preMsgTypeAsipLogFilterConfKey = "";
            String defaultMsgTypeAsipLogFilterConfValue = "true";
            switch (msgTypeCode) {
            case 1: {
                preMsgTypeAsipLogFilterConfKey = "AsipLogFilter.ab.";
                defaultMsgTypeAsipLogFilterConfValue = "false";
                break;
            }
            case 2: {
                preMsgTypeAsipLogFilterConfKey = "AsipLogFilter.da.";
                defaultMsgTypeAsipLogFilterConfValue = "false";
                break;
            }
            case 3: {
                preMsgTypeAsipLogFilterConfKey = "AsipLogFilter.bc.";
                defaultMsgTypeAsipLogFilterConfValue = "true";
                break;
            }
            case 4: {
                preMsgTypeAsipLogFilterConfKey = "AsipLogFilter.cd.";
                defaultMsgTypeAsipLogFilterConfValue = "true";
                break;
            }
            }

            String msgTypeAsipLogFilterConfValue = "true";
            String msgTypeAsipLogFilterConfKey = preMsgTypeAsipLogFilterConfKey + appName + "." + serviceName;
            msgTypeAsipLogFilterConfValue = conf.getProperty(msgTypeAsipLogFilterConfKey);

            if (msgTypeAsipLogFilterConfValue != null
                    && (msgTypeAsipLogFilterConfValue.equalsIgnoreCase("true") || msgTypeAsipLogFilterConfValue
                            .equalsIgnoreCase("false"))) {
                if (msgTypeAsipLogFilterConfValue.equalsIgnoreCase("true")) { // 配置为不允许写日时志,所以要返回不accept
                    return false;
                } else {
                    return true;
                }
            }

            msgTypeAsipLogFilterConfKey = preMsgTypeAsipLogFilterConfKey + "." + serviceName;
            msgTypeAsipLogFilterConfValue =
                    conf.getProperty(msgTypeAsipLogFilterConfKey, defaultMsgTypeAsipLogFilterConfValue);

            if (msgTypeAsipLogFilterConfValue.equalsIgnoreCase("true")) {
                return false;
            } else {
                return true;
            }
        }
        return accept;
    }

    public static boolean isWriteDb(int msgTypeCode, String errorCode, String appName, String serviceName) {
        String msgType = com.regaltec.asip.utils.IdaMsgUtils.getMsgType(msgTypeCode);
        com.regaltec.asip.utils.PropertiesMapping conf =
                new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
        String dbFilterExcept = conf.getProperty("AsipLogFilter.db.filterExcept");
        String appServce = appName + "." + serviceName;
        if (dbFilterExcept != null
                && dbFilterExcept.indexOf(appServce) >= 0
                && (dbFilterExcept.indexOf(";" + appServce + ";") > 0 || dbFilterExcept.equals(appServce)
                        || dbFilterExcept.startsWith(appServce + ";") || dbFilterExcept.endsWith(";" + appServce))) {
            return true;
        }
        appServce = "." + serviceName;
        if (dbFilterExcept != null
                && dbFilterExcept.indexOf(appServce) >= 0
                && (dbFilterExcept.indexOf(";" + appServce + ";") > 0 || dbFilterExcept.equals(appServce)
                        || dbFilterExcept.startsWith(appServce + ";") || dbFilterExcept.endsWith(";" + appServce))) {
            return true;
        }

        if (conf.getProperty("AsipLogFilter.db." + msgType + ".filterAll").equals("true")) {
            return false;
        }
        if (msgType.equals("response") && errorCode.equals("ASIP-0000")) {
            if (conf.getProperty("AsipLogFilter.db.response.ASIP-0000.filterAll").equals("true")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOnlyRecordMsgLength() {
        return isOnlyRecordMsgLength(null, null);
    }

    public static boolean isOnlyRecordMsgLength(String appName, String serviceName) {
        if (appName != null && serviceName != null && !appName.equals("") && !serviceName.equals("")) {
            com.regaltec.asip.utils.PropertiesMapping conf =
                    new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
            String dbFilterExcept = conf.getProperty("AsipLogFilter.db.filterExcept");
            String appServce = appName + "." + serviceName;
            if (dbFilterExcept != null
                    && dbFilterExcept.indexOf(appServce) >= 0
                    && (dbFilterExcept.indexOf(";" + appServce + ";") > 0 || dbFilterExcept.equals(appServce)
                            || dbFilterExcept.startsWith(appServce + ";") || dbFilterExcept.endsWith(";" + appServce))) {
                return false;
            }

            appServce = "." + serviceName;
            if (dbFilterExcept != null
                    && dbFilterExcept.indexOf(appServce) >= 0
                    && (dbFilterExcept.indexOf(";" + appServce + ";") > 0 || dbFilterExcept.equals(appServce)
                            || dbFilterExcept.startsWith(appServce + ";") || dbFilterExcept.endsWith(";" + appServce))) {
                return false;
            }
        }
        com.regaltec.asip.utils.PropertiesMapping conf =
                new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
        String recordMsgLength = conf.getProperty("AsipLogFilter.db.only.record.msgLength", "false");
        if (recordMsgLength.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static boolean isRecordMsgLength() {
        return isRecordMsgLength(null, null);
    }

    public static boolean isRecordMsgLength(String appName, String serviceName) {
        if (isOnlyRecordMsgLength(appName, serviceName)) {
            return false;
        }
        com.regaltec.asip.utils.PropertiesMapping conf =
                new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
        String recordMsgLength = conf.getProperty("AsipLogFilter.db.record.msgLength", "false");
        if (recordMsgLength.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    /*
     * 这里暂时有点乱,一会儿读servicesFilete，一会儿读conf,有时间再整理
     */
    public static boolean accept(String clientIP, String simulateFlag) {
        if (filterAll) {
            return false;
        }
        com.regaltec.asip.utils.PropertiesMapping conf =
                new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
        if (simulateFlag.equals("DEBUG")) {
            if (!conf.getProperty("AsipLogFilter.DEBUG").equals("false")) {
                return false;
            }
        }
        if (conf.getProperty("AsipLogFilter." + clientIP).equals("true")) {
            return false;
        }
        if (conf.getProperty("AsipLogFilter." + clientIP + "." + simulateFlag).equals("true")) {
            return false;
        }
        return true;
    }

    /*
     * 这里暂时有点乱,一会儿读servicesFilete，一会儿读conf,有时间再整理
     */
    public static boolean accept(String filter) {
        if (filterAll) {
            return false;
        }
        com.regaltec.asip.utils.PropertiesMapping conf =
                new com.regaltec.asip.utils.PropertiesMapping("asipconf/asip.properties");
        if (conf.getProperty("AsipLogFilter." + filter).equals("true")) {
            return false;
        }
        if (servicesFilete.get(filter) == null) {
            return true;
        }
        return false;
    }

    public boolean accept(MuleMessage message) {
        String appName = message.getInboundProperty("IDA40_ASIP_MSG_HEAD_APP_NAME", "UNKNOW");
        String serviceName = message.getInboundProperty("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", "UNKNOW");
        String clientIp = message.getInboundProperty("ASIP_REMOTE_CLIENT_ADDRESS", "0.0.0.0");
        String simulateFlag = message.getInboundProperty("IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG", "UNKNOW");
        String msgType = message.getInboundProperty("IDA40_ASIP_MSG_HEAD_MSG_TYPE", "UNKNOW");
        return accept(appName, serviceName, clientIp, simulateFlag, msgType);
    }

    public static boolean acceptFromSession(MuleMessage message) {
        String appName = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_APP_NAME", "UNKNOW");
        String serviceName = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SERVICE_NAME", "UNKNOW");
        String clientIp = message.getSessionProperty("ASIP_REMOTE_CLIENT_ADDRESS", "0.0.0.0");
        String simulateFlag = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG", "UNKNOW");
        String msgType = message.getSessionProperty("IDA40_ASIP_MSG_HEAD_MSG_TYPE", "UNKNOW");
        return accept(appName, serviceName, clientIp, simulateFlag, msgType);
    }
}
