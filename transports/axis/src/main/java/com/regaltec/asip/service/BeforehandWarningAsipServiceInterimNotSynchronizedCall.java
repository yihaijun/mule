/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import java.util.Hashtable;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-5-27 上午11:24:45</p>
 *
 * @author yihaijun
 */
public class BeforehandWarningAsipServiceInterimNotSynchronizedCall extends AsipServiceInterimNotSynchronizedCall {
    protected static Hashtable<String, AsipServiceInterimNotSynchronizedCallMonitorOne> prewarnMonitorSet = new Hashtable();
    public BeforehandWarningAsipServiceInterimNotSynchronizedCall(){
        key_minNumberOfSamples =  ".prewarn.minNumberOfSamples";
        key_inspection_cycle =  ".prewarn.inspection_cycle";
        key_exception_threshold =  ".prewarn.exception_threshold";
    }
    
    public String  getPreWarnTxt(String serviceName){
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("\r\n<service>");
        buf.append("\r\n\t<head>");
        buf.append("\r\n\t\t<sender>asip</sender>");
        buf.append("\r\n\t\t<receiver>ida</receiver>");
        buf.append("\r\n\t\t<time>");
        buf.append(com.regaltec.asip.utils.DateUtils.now());
        buf.append("</time>");
        buf.append("\r\n\t\t<service_name>BeforehandWarningAsipServiceInterimNotSynchronizedCall</service_name>");
        buf.append("\r\n\t\t<msg_type>report</msg_type>");
        buf.append("\r\n\t\t<msg_id>");
        buf.append(com.regaltec.asip.utils.IdaMsgUtils.getMsgType("event"));    
        buf.append("</msg_id>");
        buf.append("\r\n\t\t<simulate_flag>false</simulate_flag>");
        buf.append("\r\n\t\t<error_code></error_code>");
        buf.append("\r\n\t\t<error_info>");
        buf.append("Service call may be rejected");
        buf.append("\r\n\t\t</error_info>");
        buf.append("\r\n\t</head>");
        buf.append("\r\n\t<data_inf><serviceName>");

        AsipServiceInterimNotSynchronizedCallMonitorOne monitorOne = prewarnMonitorSet.get(serviceName);
        if(monitorOne.isAlreadyWarn()){
            return null;
        }
        monitorOne.setAlreadyWarn(true);
        
        buf.append(monitorOne.toXmlString());

        buf.append("\r\n\t</serviceName></data_inf>");
        buf.append("\r\n</service>");
        return buf.toString();
    }
}
