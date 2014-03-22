/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.client;

import java.io.Serializable;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2011-5-11 下午02:10:41</p>
 *
 * @author yihaijun
 */
public class AsipClientConfigItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2947201076935366191L;

    /**
     * 接口平台ip
     */
    private String ip="";

    /**
     * 备用接口平台IP
     */
    private String ip2="";

    /**
     * 接口平台端口
     */
    private int port=0;

    /**
     * 接口平台端口
     */
    private int port2=0;

    /**
     * 接口平台服务uri
     */
    private String uri="";

    /**
     * 接口平台服务uri
     */
    private String uri2="";

    /**
     * 连接超时
     */
    private int connectionTimeout = 3 * 1000;
    /**
     * 接收超时
     */
    private int receiveTimeout = 10 * 1000;

    /**
     * 
     * 构造函数
     * 
     * @param ip
     *            接口平台ip
     * @param port
     *            端口
     * @param uri
     *            服务uri
     */
    public AsipClientConfigItem(String ip, int port, String uri) {
        this.ip = ip;
        this.port = port;
        this.uri = uri;
    }

    /**
     * 
     * 构造函数
     * @param ip ip地址
     * @param port 端口
     * @param uri 服务uri
     * @param connectionTimeout 连接超时
     * @param receiveTimeout 接收超时
     */
    public AsipClientConfigItem(String ip, int port, String uri, int connectionTimeout, int receiveTimeout) {
        super();
        this.ip = ip;
        this.port = port;
        this.uri = uri;
        this.connectionTimeout = connectionTimeout;
        this.receiveTimeout = receiveTimeout;
    }

    /**
     * 
     * 构造函数
     * @param ip ip地址
     * @param port 端口
     * @param uri 服务uri
     * @param connectionTimeout 连接超时
     * @param receiveTimeout 接收超时
     * @param ip2 ip地址2
     * @param port2 端口2
     * @param uri2 服务uri2
     */
    public AsipClientConfigItem(String ip, int port, String uri, int connectionTimeout, int receiveTimeout,String ip2, int port2, String uri2) {
        super();
        this.ip = ip;
        this.port = port;
        this.uri = uri;
        this.connectionTimeout = connectionTimeout;
        this.receiveTimeout = receiveTimeout;
        this.ip2 = ip2;
        this.port2 = port2;
        this.uri2 = uri2;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip
     *            the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the ip2
     */
    public String getIp2() {
        return ip2;
    }

    /**
     * @param ip2 the ip2 to set
     */
    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri
     *            the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the connectionTimeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout
     *            the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the receiveTimeout
     */
    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    /**
     * @param receiveTimeout
     *            the receiveTimeout to set
     */
    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + connectionTimeout;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
        result = prime * result + receiveTimeout;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    /**
     * @return the port2
     */
    public synchronized int getPort2() {
        return port2;
    }

    /**
     * @param port2 the port2 to set
     */
    public synchronized void setPort2(int port2) {
        this.port2 = port2;
    }

    /**
     * @return the uri2
     */
    public synchronized String getUri2() {
        return uri2;
    }

    /**
     * @param uri2 the uri2 to set
     */
    public synchronized void setUri2(String uri2) {
        this.uri2 = uri2;
    }

    public String getAsipCxfWebServiceUrl() {
        String url = "http://" + ip + ":" + port + uri;
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AsipClientConfigItem other = (AsipClientConfigItem) obj;
        if (connectionTimeout != other.connectionTimeout || this.receiveTimeout != other.receiveTimeout) {
            return false;
        }
        if (!ip.equals(other.ip) || port != other.port || !uri.equals(other.uri)) {
            return false;
        }
        if (!ip2.equals(other.ip2) || port2 != other.port2 || !uri2.equals(other.uri2)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ip=");
        sb.append(ip);
        sb.append(", port=");
        sb.append(port);
        sb.append(", uri=");
        sb.append(uri);
        sb.append(", ip2=");
        sb.append(ip2);
        sb.append(", port2=");
        sb.append(port2);
        sb.append(", uri2=");
        sb.append(uri);
        sb.append(", connectionTimeout=");
        sb.append(connectionTimeout);
        sb.append(", receiveTimeout=");
        sb.append(receiveTimeout);
        sb.append("]");
        return sb.toString();
    }
}