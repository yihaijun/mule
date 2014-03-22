/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-5-28 下午05:48:35</p>
 *
 * @author yihaijun
 */
public class AsipServiceInterimNotSynchronizedCall {
    protected static Hashtable<String, AsipServiceInterimNotSynchronizedCallMonitorOne> monitorSet = new Hashtable();
    private Properties interimNotSynchronizedCallProperties = null;
    protected String key_minNumberOfSamples = ".minNumberOfSamples";
    protected String key_inspection_cycle = ".inspection_cycle";
    protected String key_exception_threshold = ".exception_threshold";

    public boolean isCanCall(String serviceName) {
        try {
            if (!isMonitor(serviceName)) {
                return true;
            }
            String minNumberOfSamplesStr =
                    interimNotSynchronizedCallProperties.getProperty(serviceName + key_minNumberOfSamples);
            String inspection_cycleStr =
                    interimNotSynchronizedCallProperties.getProperty(serviceName + key_inspection_cycle);
            String exception_thresholdStr =
                    interimNotSynchronizedCallProperties.getProperty(serviceName + key_exception_threshold);
            if (minNumberOfSamplesStr == null || minNumberOfSamplesStr.trim().equals("")) {
                return true;
            }
            if (inspection_cycleStr == null || inspection_cycleStr.trim().equals("")) {
                return true;
            }
            if (exception_thresholdStr == null || exception_thresholdStr.trim().equals("")) {
                return true;
            }
            AsipServiceInterimNotSynchronizedCallMonitorOne monitorOne = getMonitorOne(serviceName);
            monitorOne.registerCall();
            return monitorOne.editCanCall(minNumberOfSamplesStr, inspection_cycleStr, exception_thresholdStr);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean registerException(String serviceName, String errorCode, String errorInf) {
        try {
            if (!isMonitor(serviceName)) {
                return true;
            }
            String minNumberOfSamplesStr =
                    interimNotSynchronizedCallProperties.getProperty(serviceName + key_minNumberOfSamples);
            String inspection_cycleStr =
                    interimNotSynchronizedCallProperties.getProperty(serviceName + key_inspection_cycle);
            String exception_thresholdStr =
                    interimNotSynchronizedCallProperties.getProperty(serviceName + key_exception_threshold);
            if (minNumberOfSamplesStr == null || minNumberOfSamplesStr.trim().equals("")) {
                return true;
            }
            if (inspection_cycleStr == null || inspection_cycleStr.trim().equals("")) {
                return true;
            }
            if (exception_thresholdStr == null || exception_thresholdStr.trim().equals("")) {
                return true;
            }
            AsipServiceInterimNotSynchronizedCallMonitorOne monitorOne = getMonitorOne(serviceName);
            monitorOne.registerException();
            return monitorOne.editCanCall(minNumberOfSamplesStr, inspection_cycleStr, exception_thresholdStr);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private AsipServiceInterimNotSynchronizedCallMonitorOne getMonitorOne(String serviceName) {
        AsipServiceInterimNotSynchronizedCallMonitorOne monitorOne = null;
        Hashtable<String, AsipServiceInterimNotSynchronizedCallMonitorOne> theMonitorSet = monitorSet;
        if (key_minNumberOfSamples.equals(".prewarn.minNumberOfSamples")) {
            theMonitorSet = BeforehandWarningAsipServiceInterimNotSynchronizedCall.prewarnMonitorSet;
        }
        synchronized (theMonitorSet) {
            if (theMonitorSet.get(serviceName) == null) {
                monitorOne = new AsipServiceInterimNotSynchronizedCallMonitorOne();
                theMonitorSet.put(serviceName, monitorOne);
            } else {
                monitorOne = theMonitorSet.get(serviceName);
            }
        }
        return monitorOne;
    }

    protected boolean isMonitor(String serviceName) {
        interimNotSynchronizedCallProperties =
                new PropertiesMapping("asipconf/interimnotsynchronizedcall.properties").getProperties();
        if (interimNotSynchronizedCallProperties == null || interimNotSynchronizedCallProperties.size() <= 0) {
            return false;
        }
        return true;
    }

    public static String toTxt() {
        StringBuffer outbuf = new StringBuffer();
        synchronized (monitorSet) {
            outbuf.append("\r\n\tAsipServiceInterimNotSynchronizedCall.monitorSet.size()=" + monitorSet.size());
            Iterator it = monitorSet.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                AsipServiceInterimNotSynchronizedCallMonitorOne monitorOne = monitorSet.get(key);
                outbuf.append("\r\n\t\tmonitorOne(");
                outbuf.append(key);
                outbuf.append("):");
                outbuf.append(monitorOne.toTxt());
                outbuf.append("\r\n");
            }
            outbuf.append("\r\n");
        }
        return outbuf.toString();
    }

    public static String toXmlString() {
        StringBuffer outbuf = new StringBuffer();
        synchronized (monitorSet) {
            outbuf.append("<AsipServiceInterimNotSynchronizedCall><monitorSet><size>" + monitorSet.size() + "</size>");
            Iterator it = monitorSet.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                AsipServiceInterimNotSynchronizedCallMonitorOne monitorOne = monitorSet.get(key);
                outbuf.append("\r\n\t\t<monitorOne><name>");
                outbuf.append(key);
                outbuf.append("</name>");
                outbuf.append(monitorOne.toXmlString());
                outbuf.append("</monitorOne>\r\n");
            }
            outbuf.append("\r\n</monitorSet></AsipServiceInterimNotSynchronizedCall>");
        }
        return outbuf.toString();
    }
}
