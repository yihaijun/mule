/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.thread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>子线程基类</p>
 * <p>创建日期：2010-11-23 16:48:16</p>
 * @author 戈亮锋
 */
public class BaseSubThread extends Thread {
    protected   Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * 运行时错误日志信息
     */
    protected StringBuffer errorLog;
    /**
     * 线程执行起始时间
     */
    protected long startTime;
    /**
     * 线程执行中止时间
     */
    protected long endTime;
    /**
     * 线程执行时长
     */
    protected long sumTime;
    
    public StringBuffer getErrorLog() {
        return errorLog;
    }
    
    public void setErrorLog(StringBuffer errorLog) {
        this.errorLog = errorLog;
    }

    public void setErrorLog(String errorLog) {
        if(this.errorLog==null){
            this.errorLog = new StringBuffer();
        }
        this.errorLog.append(errorLog);;
    }
    
    public long getSumTime() {
        return sumTime;
    }

    public void setSumTime(long sumTime) {
        this.sumTime = sumTime;
    }

    
}
