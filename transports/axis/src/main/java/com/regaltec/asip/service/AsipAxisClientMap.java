/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import java.util.Iterator;
import java.util.Properties;

import org.mule.api.MuleMessage;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-4-24 下午05:34:55</p>
 *
 * @author yihaijun
 */
public class AsipAxisClientMap {
    private static Properties propertiesTh = new Properties();
    private static Properties propertiesIp = new Properties();
    
    public static String getAsipAxisCleintIp(MuleMessage message){
        if (message.getInboundProperty("MULE_REMOTE_CLIENT_ADDRESS") != null) {
            propertiesTh.put(java.lang.Thread.currentThread().getName(),System.currentTimeMillis());
            propertiesIp.put(java.lang.Thread.currentThread().getName(),message.getInboundProperty("MULE_REMOTE_CLIENT_ADDRESS"));
        }
        java.util.Hashtable<String, String> reomveKeySet = new java.util.Hashtable<String, String>();
        if(propertiesTh.size()>3000){
            synchronized (propertiesTh) {
                Iterator<Object> it = propertiesTh.keySet().iterator();
                String keyName = "";
                while (it.hasNext()) {
                    keyName = (String) it.next();
                    if (System.currentTimeMillis() - ((Long) propertiesTh.get(keyName)) > 5400000L) {
                        reomveKeySet.put(keyName, keyName);
                    }
                }
                Iterator<String> itRemove = reomveKeySet.keySet().iterator();
                keyName = "";
                while (itRemove.hasNext()) {
                    keyName = (String) itRemove.next();
                    propertiesTh.remove(keyName);
                    propertiesIp.remove(keyName);
                }
            }
        }
        return propertiesIp.getProperty(java.lang.Thread.currentThread().getName());
    }

    public static String toTxt(){
        StringBuffer outbuf = new StringBuffer();
        outbuf.append("\r\n\tAsipAxisClientMap.propertiesTh.size()="+propertiesTh.size());
        Iterator it = propertiesTh.keySet().iterator();
        while(it.hasNext()){
            Object key=it.next();
            outbuf.append("\r\n\t\tThread:");
            outbuf.append(key);
            outbuf.append(":Has experienced ");
            outbuf.append(System.currentTimeMillis()-((Long)propertiesTh.get(key)));
            outbuf.append("ms(");
            outbuf.append(propertiesIp.get(key));
            outbuf.append(")");
        }
        return outbuf.toString();
    }

}
