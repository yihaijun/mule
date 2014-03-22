/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.thread;

/**
 * <p>抽象线程接口</p>
 * <p>创建日期：2010-11-23 9:23:03</p>
 *
 * @author 戈亮锋
 */
public interface IBaseThread extends Runnable {
    /**
     * <p>设置退出条件</p>
     * @return int 
     */
    public int toBeginExit();

    /**
     * <p>线程退出时要执行的业务逻辑</p>
     * @return
     */
    public int onExit();

    /**
     * <p>获取线程名称</p>
     * @return
     */
    public String getName();
}
