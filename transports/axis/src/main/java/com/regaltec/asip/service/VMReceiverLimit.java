/*
 * <p>����: �й�����ۺϵ���ϵͳ��4��</p>
 * <p>����: ...</p>
 * <p>��Ȩ: Copyright (c) 2012</p>
 * <p>��˾: ��Ѷ���ͨ�ż������޹�˾</p>
 * <p>��ַ��http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>�����������ã�����ϸ������</p>
 * <p>�������ڣ�2012-10-13 ����10:16:30</p>
 *
 * @author yihaijun
 */
public class VMReceiverLimit {
    private static Hashtable<String, Long[]> receiverSet = new Hashtable();
    private static Long[] realTimeTotal = { new Long(0L), new Long(0L), new Long(System.currentTimeMillis()) };

    public static boolean gainReceiverPermit(String channel) {
        long maxReceiver = getMaxConfig(channel);
        if (maxReceiver <= 0L) {
            return false;
        }
        //synchronized (receiverSet) {
            Long[] numActiveReceiver = receiverSet.get(channel);
            if (numActiveReceiver == null) {
                numActiveReceiver =
                        new Long[] { new Long(1L), new Long(1L), new Long(0L), new Long(1L),
                                new Long(System.currentTimeMillis()) };
                receiverSet.put(channel, numActiveReceiver);
                addRealTimeTotal();
                return true;
            }
            numActiveReceiver[1] = numActiveReceiver[1] + 1L;
            if (numActiveReceiver[0] < maxReceiver) {
                numActiveReceiver[0] = numActiveReceiver[0] + 1L;
                addRealTimeTotal();
                recordMax(numActiveReceiver);
                return true;
            }
            numActiveReceiver[2] = numActiveReceiver[2] + 1L;
            return false;
        //}
    }

    public static void returnReceiverPermit(String channel) {
            synchronized (receiverSet) {
                Long[] numActiveReceiver = receiverSet.get(channel);
                if (numActiveReceiver == null) {
                    return;
                }
                numActiveReceiver[0] = numActiveReceiver[0] - 1L;
                if (numActiveReceiver[0] < 0L) {
                    numActiveReceiver[0] = 0L;
                }
                subtractionRealTimeTotal();
            }
    }

    private static long getMaxConfig(String channel) {
        if (channel == null || channel.equals("")) {
            return 0L;
        }
        String key = channel + ".receiver.max";
        Properties t_asip_service_element_list =
                new PropertiesMapping("asipconf/t_asip_service_element_list.properties").getProperties();
        if (t_asip_service_element_list == null || t_asip_service_element_list.getProperty(key) == null) {
            if (channel.equals("asip.vm.asip.service.router.manager.channel")
                    || channel.equals("asig.vm.router.channel")) {
                return 100L;
            }
            if (channel.indexOf("asip.vm") == 0) {
                return 80L;
            }
            if (channel.endsWith(".jms.channel") || channel.endsWith(".router.channel")) {
                return 70L;
            }
            return 20L;
        }
        try {
            return Long.parseLong(t_asip_service_element_list.getProperty(key));
        } catch (Exception e) {
            return 5L;
        }
    }

    private static void addRealTimeTotal() {
        realTimeTotal[0] = realTimeTotal[0] + 1L;
        if (realTimeTotal[0] >= realTimeTotal[1]) {
            realTimeTotal[1] = realTimeTotal[0];
            realTimeTotal[2] = System.currentTimeMillis();
        }
    }

    private static void subtractionRealTimeTotal() {
        realTimeTotal[0] = realTimeTotal[0] - 1L;
        if (realTimeTotal[0] < 0L) {
            realTimeTotal[0] = 0L;
        }
    }

    private static void recordMax(Long[] receiverTotal) {
        if (receiverTotal[0] >= receiverTotal[3]) {
            receiverTotal[3] = receiverTotal[0];
            receiverTotal[4] = System.currentTimeMillis();
        }
    }

    public static String toTxt() {
        // StringBuffer outbuf = new StringBuffer();
        // outbuf.delete(0,outbuf.length());
        StringBuffer tabFormat = new StringBuffer();
        tabFormat.delete(0, tabFormat.length());
        
        tabFormat.append("<table border=\"1\">");
        tabFormat.append("<tr>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#24403;&#21069;")); //��ǰ
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#26368;&#22823;")); //���
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#26368;&#22823;&#21457;&#29983;&#26102;&#38388;"));//�����ʱ��
        tabFormat.append("</th>");
        tabFormat.append("</tr>");

        tabFormat.append("<tr>");
        tabFormat.append("<td>");
        tabFormat.append(markup(""+realTimeTotal[0]));
        tabFormat.append("</td>");
        tabFormat.append("<td>");
        tabFormat.append(markup("" + realTimeTotal[1]));
        tabFormat.append("</td>");
        tabFormat.append("<td>");
        tabFormat.append(markup(toStrFromDate(new Date(realTimeTotal[2]),null)));
        tabFormat.append("</td>");
        tabFormat.append("</tr>");

        tabFormat.append("</table>");
        
        tabFormat.append("\r\n");

        tabFormat.append("<table border=\"1\">");
        tabFormat.append("<tr>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#36890;&#36947;"));//ͨ��
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#24403;&#21069;"));//��ǰ
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#25298;&#32477;"));//�ܾ�
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#26368;&#22823;&#20801;&#35768;"));//�������
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#21382;&#21490;&#26368;&#22823;"));//��ʷ���
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#26368;&#22823;&#21457;&#29983;&#26102;&#38388;"));//�����ʱ��
        tabFormat.append("</th>");
        tabFormat.append("<th scope=\"col\">");
        tabFormat.append(markup2("&#21512;&#35745;"));//�ϼ�
        tabFormat.append("</th>");
        tabFormat.append("</tr>");
        synchronized (receiverSet) {
            // outbuf.append("\r\n\tVMReceiverLimit.receiverSet.size()=" + receiverSet.size());
            Iterator it = receiverSet.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                long maxReceiver = getMaxConfig((String) key);
                Long[] numActiveReceiver = receiverSet.get(key);
                // outbuf.append("\r\n\t\tVMReceiver(");
                // outbuf.append(key);
                // outbuf.append(")");
                // for (int i = key.length(); i < 50; i++) {
                // outbuf.append("-");
                // }
                // outbuf.append("��ǰ����:" + numActiveReceiver[0]);
                // outbuf.append("��\t������?��:" + maxReceiver);
                // outbuf.append("��\t�ܾ�" + numActiveReceiver[2]);
                // outbuf.append("��\t�ܹ��ӵ�" + numActiveReceiver[1] + "������");

                tabFormat.append("<tr>");
                tabFormat.append("<td>");
                tabFormat.append(markup(key));
                tabFormat.append("</td>");
                tabFormat.append("<td>");
                tabFormat.append(markup("" + numActiveReceiver[0]));
                tabFormat.append("</td>");
                tabFormat.append("<td>");
                tabFormat.append(markup("" + numActiveReceiver[2]));
                tabFormat.append("</td>");
                tabFormat.append("<td>");
                tabFormat.append(markup("" + maxReceiver));
                tabFormat.append("</td>");
                tabFormat.append("<td>");
                tabFormat.append(markup("" + numActiveReceiver[3]));
                tabFormat.append("</td>");
                tabFormat.append("<td>");
                tabFormat.append(markup(toStrFromDate(new Date(numActiveReceiver[4]),null)));
                tabFormat.append("</td>");
                tabFormat.append("<td>");
                tabFormat.append(markup("" + numActiveReceiver[1]));
                tabFormat.append("</td>");
                tabFormat.append("</tr>");
            }
            // outbuf.append("\r\n");
        }
        // return outbuf.toString();
        tabFormat.append("</table>");
        return tabFormat.toString();
    }

    private static String markup(String text) {
        if (text == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
            case '<':
                buffer.append("&lt;");
                break;
            case '&':
                buffer.append("&amp;");
                break;
            case '>':
                buffer.append("&gt;");
                break;
            case '"':
                buffer.append("&quot;");
                break;
            default:
                buffer.append(c);
                break;
            }
        }
        return buffer.toString();
    }

    private static String markup2(String text) {
			return text;
		}

    public static final String  DEFAULT_DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
    /**
     * Dateת��Ϊ�ַ�
     * @param date
     * @param pattern
     * @return
     */
    public static String toStrFromDate(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        if(StringUtils.isBlank(pattern)||StringUtils.isEmpty(pattern)){
                pattern = DEFAULT_DATE_FORMATE;
        }
        String tmp = new SimpleDateFormat(pattern).format(date);
                return tmp;
    }
}
