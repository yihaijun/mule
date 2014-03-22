/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.manager.api.thread;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2010-11-20 下午03:12:34</p>
 * @author yihaijun
 */
public abstract class AbstractBaseThread implements IBaseThread {

    /**
     * 调用协议适配器时，存储可能的引起的异常信息
     */
    protected StringBuffer errorBuffer;
    /**
     * 摘要
     */
    protected StringBuffer summaryBuffer;
    /**
     * 详情
     */
    protected StringBuffer detailBuffer;

    /**
     * 线程运行起始时间
     */
    protected long startTime;

    /**
     * 线程终止时间
     */
    protected long endTime;

    /**
     * 批量操作是否成功
     */
    protected boolean isAllDoneOk = true;

    public StringBuffer getErrorBuffer() {
        return errorBuffer;
    }

    public void setErrorBuffer(StringBuffer errorBuffer) {
        this.errorBuffer = errorBuffer;
    }

    public StringBuffer getSummaryBuffer() {
        return summaryBuffer;
    }

    public void setSummaryBuffer(StringBuffer summaryBuffer) {
        this.summaryBuffer = summaryBuffer;
    }

    public StringBuffer getDetailBuffer() {
        return detailBuffer;
    }

    public void setDetailBuffer(StringBuffer detailBuffer) {
        this.detailBuffer = detailBuffer;
    }

    public boolean isAllDoneOk() {
        return isAllDoneOk;
    }

    public void setAllDoneOk(boolean isAllDoneOk) {
        this.isAllDoneOk = isAllDoneOk;
    }

    /**
     * <p>获取线程名称</p>
     * @return
     */
    public String getName() {
        return this.getClass().getName();
    }
}
