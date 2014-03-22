/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.regaltec.asip.manager.api.util.SystemUtil;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-5-12 下午01:42:36</p>
 *
 * @author yihaijun
 */
public class AsipClientConfig {
    private static Hashtable<String, AsipClientConfigItem> config = new Hashtable<String, AsipClientConfigItem>();
    private static String configFile = "";
    private static int thresholdtime=3000;
    private static String protocolhandlerclass="com.regaltec.ida40.asip.client.HTTPProtocolHandler";
    private static long lastModified = 0;

    public synchronized static AsipClientConfigItem getAsipClientConfigItem(String appName, String serviceName) {
        if (serviceName == null) {
            serviceName = "UNKNOW";
        }
        if (config.size() == 0 || isModifiedConfig()) {
            loadConfig();
        }
        String keyName = "asip." + appName + "." + serviceName;
        if (config.get(StringUtils.upperCase(keyName)) != null) {
            return config.get(StringUtils.upperCase(keyName));
        }
        keyName = "asip." + appName;
        if (config.get(StringUtils.upperCase(keyName)) != null) {
            return config.get(StringUtils.upperCase(keyName));
        }
        keyName = "asip";
        if (config.get(StringUtils.upperCase(keyName)) != null) {
            return config.get(StringUtils.upperCase(keyName));
        }
        return new AsipClientConfigItem("127.0.0.1", 8000, "/asip/services/AsipService");
    }

    //判断配置文件有没改变,如果有改变就重新加载配置
    private static boolean isModifiedConfig() {
        if (configFile == null || configFile.equals("") || lastModified == 0){
            return false;
        }
        File f = new File(configFile);
        if (f.lastModified() > lastModified){
            return true;
        }
        return false;
    }
    
    private static Properties loadPropertiesFromConfig() {
        Logger logger = Logger.getLogger(AsipClientConfig.class.getName());
        if (configFile == null || configFile.equals("")) {
            String configParentPath = "";
            String asipClientHome = SystemUtil.getEnvValueByName("ASIP_CLIENT_HOME");
            if (asipClientHome == null || asipClientHome.equals("")) {
                URL url = AsipClientConfig.class.getClassLoader().getResource("");
                logger.info("AsipClientConfig.class.getClassLoader().getResource(\"\")=" + url);
                if (url != null) {
                    configFile = url.getPath();
                }
            }else{
                configParentPath = asipClientHome;
            }
            if(!configParentPath.endsWith("/") && !configParentPath.equals("\\")){
                configParentPath = configParentPath + "/";
            }
            configFile = configParentPath + "asip-client-config.properties";
        }
        Properties p = new Properties();
        logger.info("configFile=" + configFile);
        File f = new File(configFile);
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(f);
            p.load(fi);
            lastModified=f.lastModified() ;
        } catch (FileNotFoundException e1) {
            // e1.printStackTrace();
        } catch (IOException e1) {
            // e1.printStackTrace();
        }finally{
            if(fi != null){
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // InputStream configStream = AsipClientConfig.class.getResourceAsStream(configFile);
        // try {
        // if (configStream != null) {
        // p.load(configStream);
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // p = null;
        // } finally {
        // try {
        // if (configStream != null) {
        // configStream.close();
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        return p;
    }

    public synchronized static String loadConfig(String configFileName, String ip, int port, String uri,
            int connectionTimeout, int receiveTimeout, String ip2, int port2, String uri2) {
        configFile = configFileName;
        return loadConfig(ip, port, uri, connectionTimeout, receiveTimeout, ip2, port2, uri2);
    }

    public synchronized static String loadConfig() {
        return loadConfig("");
    }

    public synchronized static String loadConfig(String configFileName) {
        configFile=configFileName;
        return loadConfig("127.0.0.1", 8000, "/asip/services/AsipService", 3000, 10000, "127.0.0.1", 8000,
                "/asip/services/AsipService");
    }
    
    public synchronized static String loadConfig(String ip, int port, String uri, int connectionTimeout,
            int receiveTimeout, String ip2, int port2, String uri2) {
        removeAllConfig();
        Properties properties = loadPropertiesFromConfig();

        try {
            thresholdtime = Integer.parseInt(properties.getProperty("ASIPCLIENT.THRESHOLDTIME","3000"));
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
        try {
            protocolhandlerclass = properties.getProperty("ASIPCLIENT.PROTOCOLHANDLERCLASS","com.regaltec.ida40.asip.client.HTTPProtocolHandler");
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        }

        
        Hashtable<String, AsipClientConfigItem> defaultConfig = new Hashtable<String, AsipClientConfigItem>();
        loadDefaultConfig("ASIP", properties, defaultConfig, ip, port, uri, connectionTimeout, receiveTimeout, ip2,
                port2, uri2);

        Iterator<Object> it = properties.keySet().iterator();
        String keyName = "";
        String[] confItemSplitToArry = null;
        String keyPrefixForDefault = "ASIP";
        String keyPrefix = "";
        AsipClientConfigItem defaultAsipClientConfigItem = defaultConfig.get("ASIP");
        while (it.hasNext()) {
            keyName = (String) it.next();
            confItemSplitToArry = keyName.split("\\.");
            if (confItemSplitToArry.length < 3 || !confItemSplitToArry[0].equalsIgnoreCase("ASIP")) {
                continue;
            }
            keyPrefixForDefault = confItemSplitToArry[0] + "." + confItemSplitToArry[1];
            if (confItemSplitToArry.length == 3) {
                loadDefaultConfig(keyPrefixForDefault, properties, defaultConfig, ip, port, uri, connectionTimeout,
                        receiveTimeout, ip2, port2, uri2);
                continue;
            }
            if (confItemSplitToArry.length > 4) {
                continue;
            }
            if (defaultConfig.get(keyPrefixForDefault) != null) {
                defaultAsipClientConfigItem = defaultConfig.get(keyPrefixForDefault);
            } else {
                defaultAsipClientConfigItem = defaultConfig.get("ASIP");
            }

            ip = defaultAsipClientConfigItem.getIp();
            ip2 = defaultAsipClientConfigItem.getIp2();
            port = defaultAsipClientConfigItem.getPort();
            port2 = defaultAsipClientConfigItem.getPort2();
            uri = defaultAsipClientConfigItem.getUri();
            uri2 = defaultAsipClientConfigItem.getUri2();
            connectionTimeout = defaultAsipClientConfigItem.getConnectionTimeout();
            receiveTimeout = defaultAsipClientConfigItem.getReceiveTimeout();

            keyPrefix = confItemSplitToArry[0] + "." + confItemSplitToArry[1] + "." + confItemSplitToArry[2];
            ip = properties.getProperty(keyPrefix + ".IP", ip);
            ip2 = properties.getProperty(keyPrefix + ".IP2", ip2);
            try {
                port = Integer.parseInt(properties.getProperty(keyPrefix + ".PORT", "" + port));
                port2 = Integer.parseInt(properties.getProperty(keyPrefix + ".PORT2", "" + port2));
            } catch (Exception e) {
                port = 8000;
                port2 = 8000;
            }
            uri = properties.getProperty(keyPrefix + ".URI", uri);
            uri2 = properties.getProperty(keyPrefix + ".URI2", uri2);
            try {
                connectionTimeout = Integer.parseInt(properties.getProperty(keyPrefix + ".DEFAULTCONNECTIONTIMEOUT", ""
                        + connectionTimeout));
                receiveTimeout = Integer.parseInt(properties.getProperty(keyPrefix + ".RECEIVETIMEOUT", ""
                        + receiveTimeout));
            } catch (Exception e) {
                connectionTimeout = 3000;
                receiveTimeout = 10000;
            }

            AsipClientConfigItem asipClientConfigItem = new AsipClientConfigItem(ip, port, uri, connectionTimeout,
                    receiveTimeout, ip2, port2, uri2);
            config.put(keyPrefix, asipClientConfigItem);
        }
        String configText = configToText();
        Logger logger = Logger.getLogger(AsipClientConfig.class.getName());
        logger.info(configText);
        return configText;
    }

    private synchronized static AsipClientConfigItem loadDefaultConfig(String keyPrefix, Properties properties,
            Hashtable<String, AsipClientConfigItem> defaultConfig, String ip, int port, String uri,
            int connectionTimeout, int receiveTimeout, String ip2, int port2, String uri2) {
        if (defaultConfig.get(keyPrefix) != null) {
            return defaultConfig.get(keyPrefix);
        }
        String defaultIp = ip;
        String defaultIp2 = ip2;
        int defaultPort = port;
        int defaultPort2 = port2;
        String defaultUri = uri;
        String defaultUri2 = uri2;
        int defaultConnectionTimeout = connectionTimeout;
        int defaultReceiveTimeout = receiveTimeout;
        if (!keyPrefix.equalsIgnoreCase("ASIP")) {
            AsipClientConfigItem defaultMainAsipClientConfigItem = defaultConfig.get("ASIP");
            defaultIp = defaultMainAsipClientConfigItem.getIp();
            defaultIp2 = defaultMainAsipClientConfigItem.getIp2();
            defaultPort = defaultMainAsipClientConfigItem.getPort();
            defaultPort2 = defaultMainAsipClientConfigItem.getPort2();
            defaultUri = defaultMainAsipClientConfigItem.getUri();
            defaultUri2 = defaultMainAsipClientConfigItem.getUri2();
            defaultConnectionTimeout = defaultMainAsipClientConfigItem.getConnectionTimeout();
            defaultReceiveTimeout = defaultMainAsipClientConfigItem.getReceiveTimeout();
        }

        defaultIp = properties.getProperty(keyPrefix + ".IP", defaultIp);
        defaultIp2 = properties.getProperty(keyPrefix + ".IP2", defaultIp2);
        try {
            defaultPort = Integer.parseInt(properties.getProperty(keyPrefix + ".PORT", "" + defaultPort));
            defaultPort2 = Integer.parseInt(properties.getProperty(keyPrefix + ".PORT2", "" + defaultPort2));
        } catch (Exception e) {
            defaultPort = 8000;
            defaultPort2 = 8000;
        }
        defaultUri = properties.getProperty(keyPrefix + ".URI", defaultUri);
        defaultUri2 = properties.getProperty(keyPrefix + ".URI2", defaultUri2);
        try {
            defaultConnectionTimeout = Integer.parseInt(properties.getProperty(keyPrefix + ".DEFAULTCONNECTIONTIMEOUT",
                    "" + defaultConnectionTimeout));
            defaultReceiveTimeout = Integer.parseInt(properties.getProperty(keyPrefix + ".RECEIVETIMEOUT", ""
                    + defaultReceiveTimeout));
        } catch (Exception e) {
            defaultConnectionTimeout = 3000;
            defaultReceiveTimeout = 10000;
        }

        AsipClientConfigItem defaultAsipClientConfigItem = new AsipClientConfigItem(defaultIp, defaultPort, defaultUri,
                defaultConnectionTimeout, defaultReceiveTimeout, defaultIp2, defaultPort2, defaultUri2);
        defaultConfig.put(keyPrefix, defaultAsipClientConfigItem);
        config.put(keyPrefix, defaultAsipClientConfigItem);
        return defaultAsipClientConfigItem;
    }

    public synchronized static String configToText() {
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        buf.append("configFile=");
        buf.append(configFile);
        buf.append("\r\n");
        java.util.Iterator<String> it = config.keySet().iterator();
        String keyName = "";
        while (it.hasNext()) {
            keyName = it.next();
            buf.append(keyName);
            for (int i = keyName.length(); i < 40; i++) {
                buf.append(" ");
            }
            buf.append(config.get(keyName).toString());
            buf.append("\r\n");
        }
        return buf.toString();
    }

    private static void removeAllConfig() {
        java.util.Iterator<String> it = config.keySet().iterator();
        while (it.hasNext()) {
            config.remove(it.next());
        }
    }

    /**
     * @return the thresholdtime
     */
    public static synchronized int getThresholdtime() {
        return thresholdtime;
    }

    /**
     * @return the protocolhandlerclass
     */
    public static synchronized String getProtocolhandlerclass() {
        return protocolhandlerclass;
    }
}
