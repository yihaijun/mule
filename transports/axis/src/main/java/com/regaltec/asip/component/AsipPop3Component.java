/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.component;

import org.mule.api.MuleEvent;
import org.mule.component.AbstractComponent;

import com.regaltec.asip.manager.api.util.AsipPop3Util;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-1-4 下午12:45:38</p>
 *
 * @author yihaijun
 */
public class AsipPop3Component extends AbstractComponent {
    private AsipPop3Util opt = new AsipPop3Util();

    @Override
    protected Object doInvoke(MuleEvent arg0) throws Exception {
        return opt.receive();
    }

    /**
     * 构造函数,初始化一个MimeMessage对象
     */
    public AsipPop3Component() {
    }

    /**
     * @return the saveAttachPath
     */
    public String getSaveAttachPath() {
        return opt.getSaveAttachPath();
    }

    /**
     * @param saveAttachPath the saveAttachPath to set
     */
    public void setSaveAttachPath(String saveAttachPath) {
        opt.setSaveAttachPath(saveAttachPath);
    }

    /**
     * @return the host
     */
    public String getHost() {
        return opt.getHost();
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        opt.setHost(host);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return opt.getUsername();
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        opt.setUsername(username);
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return opt.getPassword();
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        opt.setPassword(password);
    }

    /**
     * @return the port
     */
    public int getPort() {
        return opt.getPort();
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        opt.setPort(port);
    }
}
