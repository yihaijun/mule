/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mule.api.MuleMessage;

import com.regaltec.asip.manager.api.util.SystemUtil;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-11-22 下午01:18:58</p>
 *
 * @author yihaijun
 */
public class IdaMsgUtils {
    private static int msgIndex = 0;
    private static int msgEventIndex = 0;
    private static int msgAlarmIndex = 0;

    private static long asipCallBegin = 0;
    private static long asipCallReturn = 0;

    private static long asigCallBegin = 0;
    private static long asigCallReturn = 0;

    /**
         * 
         * <p>取群集唯一新消息ID。</p>
         * @return 群集新唯一消息ID
         * select  (l.record_id- mod(l.record_id,10000000000))/10000000000 d, 
           (mod(l.record_id,10000000000) - mod(mod(l.record_id,10000000000),360000000))/360000000 h,
           mod(((mod(l.record_id,10000000000) - mod(l.record_id,100000))/100000-  mod((mod(l.record_id,10000000000) - mod(l.record_id,100000))/100000,60))/60,60) m,
           mod((mod(l.record_id,1000000000)-mod(l.record_id,100000))/100000,60) s ,
           (mod(l.record_id,100000) - mod(l.record_id,100))/100 msgindex ,l.*
           from t_asip_interface_log l
           --求整?
           round((sysdate_end-syadate_begin)*24*60)求相差分钟整数
         */
    public synchronized static long getNewMsgId() {
        return getNewMsgId("msg");
    }

    public synchronized static long getNewMsgId(String flag) {
        int multiple = 0;
        try {
            String ip = SystemUtil.getLocalHostAddress();
            String ip4 = ip.split("\\.")[3];
            if (ip4.length() > 2) {
                ip4 = ip4.substring(1);
            }
            multiple = Integer.parseInt(ip4);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        int index = 0;
        if (flag.equals("msg")) {
            if (msgIndex >= 99000) {
                msgIndex = 0;
            }
            msgIndex += 100;
            index = msgIndex;
        } else if (flag.equals("event")) {
            if (msgEventIndex >= 99000) {
                msgEventIndex = 0;
            }
            msgEventIndex += 100;
            index = msgEventIndex;
        } else if (flag.equals("alarm")) {
            if (msgAlarmIndex >= 99000) {
                msgAlarmIndex = 0;
            }
            msgAlarmIndex += 100;
            index = msgAlarmIndex;
        }
        Date currentDate = new Date();
        int day = Integer.parseInt(new SimpleDateFormat("DDD").format(currentDate)) % 100;
        int h = Integer.parseInt(new SimpleDateFormat("HH").format(currentDate));
        int M = Integer.parseInt(new SimpleDateFormat("mm").format(currentDate));
        int s = Integer.parseInt(new SimpleDateFormat("ss").format(currentDate));

        long timeflag = day * 100000 + h * 3600 + M * 60 + s;

        return timeflag * 100000 + index + multiple;
    }

    /**
     * 
     * <p>取不带-的GUID。</p>
     * @return
     */
    public static String getUUID() {
        UUID uuid = java.util.UUID.randomUUID();
        String result = uuid.toString().replaceAll("-", "");
        return result;
    }

    public static int getMsgType(String msgType) {
        // 1-请求、2-响应、3-异常(消息发送或接收异常)';
        if (msgType.equals("request")) {
            return 1;
        } else if (msgType.equals("response")) {
            return 2;
        } else if (msgType.equals("request_bc")) {
            return 3;
        } else if (msgType.equals("response_cd")) {
            return 4;
        } else if (msgType.equals("execption")) {
            return 5;
        } else {
            return -1;
        }
    }

    public static String getMsgType(int msgType) {
        // 1-请求、2-响应、3-异常(消息发送或接收异常)';
        if (msgType == 1) {
            return "request";
        } else if (msgType == 2) {
            return "response";
        } else if (msgType == 3) {
            return "request_bc";
        } else if (msgType == 4) {
            return "response_cd";
        } else if (msgType == 5) {
            return "execption";
        } else {
            return "unknow";
        }
    }

    /**
     * @return the asipCallBegin
     */
    public static synchronized long getAsipCallBegin() {
        return asipCallBegin;
    }

    /**
     * @param asipCallBegin the asipCallBegin to set
     */
    public static synchronized void addAsipCallBegin() {
        asipCallBegin++;
    }

    /**
     * @return the asipCallReturn
     */
    public static synchronized long getAsipCallReturn() {
        return asipCallReturn;
    }

    /**
     * @param asipCallReturn the asipCallReturn to set
     */
    public static synchronized void addAsipCallReturn() {
        asipCallReturn++;
    }

    /**
     * @return the asigCallBegin
     */
    public static synchronized long getAsigCallBegin() {
        return asigCallBegin;
    }

    /**
     * @param asigCallBegin the asipCallBegin to set
     */
    public static synchronized void addAsigCallBegin() {
        asigCallBegin++;
    }

    /**
     * @return the asigCallReturn
     */
    public static synchronized long getAsigCallReturn() {
        return asigCallReturn;
    }

    /**
     * @param asigCallReturn the asipCallReturn to set
     */
    public static synchronized void addAsigCallReturn() {
        asigCallReturn++;
    }

    public static String getAsipNullRetun(String error_code, String error_info, long timeConsumingAB,
            long timeConsumingBC, long timeConsumingCD) {
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("\r\n<service>");
        buf.append("\r\n\t<head>");
        buf.append("\r\n\t\t<sender>IDA40_ASIP_MSG_HEAD_SENDER</sender>");
        buf.append("\r\n\t\t<receiver>IDA40_ASIP_MSG_HEAD_RECEIVER</receiver>");
        buf.append("\r\n\t\t<time>IDA40_ASIP_MSG_HEAD_TIME</time>");
        buf.append("\r\n\t\t<service_name>IDA40_ASIP_MSG_HEAD_SERVICE_NAME</service_name>");
        buf.append("\r\n\t\t<msg_type>response</msg_type>");
        buf.append("\r\n\t\t<msg_id>IDA40_ASIP_MSG_HEAD_MSG_ID</msg_id>");
        buf.append("\r\n\t\t<simulate_flag>IDA40_ASIP_MSG_HEAD_SIMULATE_FLAG</simulate_flag>");
        buf.append("\r\n\t\t<error_code>");
        buf.append(org.apache.commons.lang.StringEscapeUtils.escapeXml(error_code));
        buf.append("</error_code>");
        buf.append("\r\n\t\t<error_info>");
        buf.append(org.apache.commons.lang.StringEscapeUtils.escapeXml(error_info));
        buf.append("</error_info>");

        buf.append("\r\n\t\t<time_consuming_ab>");
        buf.append(timeConsumingAB);
        buf.append("</time_consuming_ab>");

        buf.append("\r\n\t\t<time_consuming_bc>");
        buf.append(timeConsumingBC);
        buf.append("</time_consuming_bc>");

        buf.append("\r\n\t\t<time_consuming_cd>");
        buf.append(timeConsumingCD);
        buf.append("</time_consuming_cd>");

        buf.append("\r\n\t</head>");
        buf.append("\r\n\t<data_info>\r\n\t</data_info>");
        buf.append("\r\n</service>");
        return buf.toString();
    }

    public static synchronized Map<String, Object> getPropertyFormSession(MuleMessage message) {
        Map<String, Object> properties = new Hashtable<String, Object>();
        Set<String> set = message.getSessionPropertyNames();
        Iterator<String> it = set.iterator();
        String key;
        Object value = "";
        while (it.hasNext()) {
            key = it.next();
            value = message.getSessionProperty(key);
            properties.put(key, value);
        }
        return properties;
    }
}
