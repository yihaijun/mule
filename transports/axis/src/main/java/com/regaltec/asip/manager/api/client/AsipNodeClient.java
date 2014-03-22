/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.client;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.regaltec.asip.manager.api.client.soap.SoapClient;
import com.regaltec.asip.manager.api.client.soap.stub.AsipServiceSoapBindingStub;
import com.regaltec.asip.manager.api.client.soap.stub.AsipService_PortType;
import com.regaltec.asip.manager.api.client.soap.stub.AsipService_ServiceLocator;
import com.regaltec.ida40.asip.client.AsipClient;

/**
 * <p>综合调度系统调用asip接口平台</p>
 * <p>创建日期：2010-8-30 上午09:51:25</p>
 *
 * @author yihaijun
 */
/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-8-30 上午10:32:09</p>
 *
 * @author yihaijun
 */
public class AsipNodeClient {
    private Logger log = Logger.getLogger(this.getClass().getName());

    private String url = "";
    private int receiveTimeout = 60000;
    private int connectTimeout = 15000;
    private int timeout = 0;
    
    public AsipNodeClient() {
        url = "http://127.0.0.1:8000/asip/services/AsipService?wsdl";
    }

    public AsipNodeClient(AsipClientConfigItem configItem) {
        url = configItem.getAsipCxfWebServiceUrl();
        receiveTimeout = configItem.getReceiveTimeout();
        connectTimeout = configItem.getConnectionTimeout();
        timeout = connectTimeout + receiveTimeout;
    }

    public AsipNodeClient(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>综合调度系统调用asip接口平台的唯一方法。</p>
     * @param businessServiceName    指定业务服务名称
     * @param businessServiceParam   服务请求参数组
     * @param requestContextName     一般填空,但在需使用同一个类实例多个函数配合时就要使用
     * @return                       xml字符串：
    <?xml version="1.0" encoding="GB2312"?>
    <service>
        <head>
                <sender>XXXXXX</sender>
                <receiver>XXXXXX</receiver>
                <time>XXXX-XX-XX XX:XX:XX</time>
                <service_name>XXXXXX</service_name>
                <msg_type>request</msg_type>
                <msg_id>XXXXXX</msg_id>
                <error_code>XXXXXX</error_code> 
        </head>
        <data_info>
        </data_info>
    </service>  
     */
    public String call(String businessServiceName, String businessServiceParam, String requestContextName) {
        return call(businessServiceName, businessServiceParam, requestContextName, timeout, connectTimeout);
    }

    public String call(String businessServiceName, String businessServiceParam, String requestContextName, int designatedTimeout,
            int designatedConnectTimeout) {
//        SoapClient client = new SoapClient();
//        client.initiate(url, "admin", "admin", "AsipService", "AsipService", timeout, connectTimeout);
//        StringBuffer buf = new StringBuffer();
//        buf.delete(0, buf.length());
//        int ret = client.callFunFor3StringParam("call", businessServiceName, businessServiceParam, requestContextName,
//                buf);
//        if (ret < 0) {
//            log.error("callFunFor3StringParam failed:ret=" + ret + ",result=" + buf.toString());
//            String error_info = buf.toString();
//            // String error_info = org.apache.commons.lang.StringEscapeUtils.escapeXml(buf.toString());
//            return "<service><head><error_code>-1</error_code><error_info>" + error_info
//                    + "</error_info></head></service>";
//        }
//        return buf.toString();
        
//        AsipService_ServiceLocator serviceLocator = new AsipService_ServiceLocator();
//        AsipService_PortType service = null;
//        try {
//            if(url.indexOf("?wsdl")>0){
//                url = url.replace("?wsdl", "");
//            }
//            service = serviceLocator.getAsipService(new java.net.URL(url));
//        } catch (MalformedURLException e) {
////            e.printStackTrace();
//            log.error("getAsipService("+url+") MalformedURLException:"+e.toString());
//        } catch (ServiceException e) {
////            e.printStackTrace();
//            log.error("getAsipService("+url+") ServiceException:"+e.toString());
//        }
//        ((AsipServiceSoapBindingStub) service).setTimeout(designatedTimeout);
//        try {
//            return service.call(businessServiceName, businessServiceParam, requestContextName);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            return "<RemoteException>"+org.apache.commons.lang.StringEscapeUtils.escapeXml(e.toString())+"</RemoteException>";
//        }
//    }

        String ip = StringUtils.substringBetween(url,"http://",":");
        int port = Integer.parseInt(StringUtils.substringBetween(url,ip+":","/"));
        String uri= "/asip/services/AsipService";
        AsipClientConfigItem configItem = new AsipClientConfigItem(ip,port,uri,designatedConnectTimeout,designatedTimeout);
        AsipClient client = new  AsipClient(configItem);
        return client.call(businessServiceName, businessServiceParam, null);
    }
}
