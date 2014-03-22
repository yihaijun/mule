/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import java.io.Serializable;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-5-28 下午06:40:25</p>
 *
 * @author yihaijun
 */
public class AsipServiceInterimNotSynchronizedCallMonitorOne implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private long call = 0L;
    private long exception = 0L;
    private boolean canCall = true;
    private long startTotalTime = System.currentTimeMillis();
    private long exceptionStartTime = 0L;

    private long totalCall = 0L;
    private long totalException = 0L;
    private long totalNotCanCall = 0L;

    private long minNumberOfSamples = 0L;
    private long inspection_cycle = 0L;
    private long exception_threshold = 0L;
    private boolean alreadyWarn = false;

    private long initTotal() {
        call = 0;
        exception = 0;
        startTotalTime = System.currentTimeMillis();
        alreadyWarn = false;
        return 0;
    }

    /**
     * @return the call
     */
    public long getCall() {
        return call;
    }

    /**
     * @param call the call to set
     */
    public void setCall(long call) {
        this.call = call;
    }

    /**
     * @return the exception
     */
    public long getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(long exception) {
        this.exception = exception;
    }

    /**
     * @return the canCall
     */
    public boolean isCanCall() {
        return canCall;
    }

    /**
     * @param canCall the canCall to set
     */
    public void setCanCall(boolean canCall) {
        this.canCall = canCall;
    }

    public boolean editCanCall(String minNumberOfSamplesStr, String inspection_cycleStr, String exception_thresholdStr) {
        try {
            minNumberOfSamples = Long.parseLong(minNumberOfSamplesStr);
            inspection_cycle = Long.parseLong(inspection_cycleStr) * 1000L;
            exception_threshold = Long.parseLong(exception_thresholdStr);
            return editCanCall();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean editCanCall() {
        return editCanCall(false);
    }

    private boolean editCanCall(boolean query) {
        long theCall = call;
        if (query) {
            theCall++;//NMA-147 查询服务是否可以调用以下一次可能调用的情况为依据
        }
        try {
            if (theCall < minNumberOfSamples) {
                if (exception_threshold == 0L) {
                    canCall = true;
                }
                if (System.currentTimeMillis() - startTotalTime >= inspection_cycle) {
                    canCall = true;
                    if (exceptionStartTime > 0L) {
                        totalNotCanCall = totalNotCanCall + (System.currentTimeMillis() - exceptionStartTime);
                    }
                    exceptionStartTime = 0L;
                    initTotal();
                }
            } else {
                if (exception_threshold == 0L) {
                    if (minNumberOfSamples == 0L) {
                        canCall = true;
                    } else {
                        canCall = false;
                    }
                } else if ((exception * 100 / theCall) < exception_threshold) {
                    canCall = true;
                    if (exceptionStartTime > 0L) {
                        totalNotCanCall = totalNotCanCall + (System.currentTimeMillis() - exceptionStartTime);
                    }
                    exceptionStartTime = 0L;
                } else {
                    canCall = false;

                    if (exceptionStartTime <= 0L) {
                        exceptionStartTime = System.currentTimeMillis();
                        initTotal();
                    }
                }
                if (System.currentTimeMillis() - startTotalTime > inspection_cycle) {
                    initTotal();
                }
            }
            return canCall;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public long registerCall() {
        call++;
        totalCall++;
        return call;
    }

    public long registerException() {
        exception++;
        totalException++;
        return exception;
    }

    public String toTxt() {
        editCanCall(true);
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        buf.append("\r\n\t\tcall = " + call);
        buf.append(",exception = " + exception);
        buf.append(",canCall=" + canCall);
        buf.append("\r\n\t\ttotalTime = " + (System.currentTimeMillis() - startTotalTime));
        buf.append(",exceptionStartTime = " + exceptionStartTime);
        buf.append("\r\n\t\ttotalCall = " + totalCall);
        buf.append(",totalException = " + totalException);
        if (exceptionStartTime > 0L) {
            buf.append(",totalNotCanCall=" + (totalNotCanCall + (System.currentTimeMillis() - exceptionStartTime)));
        } else {
            buf.append(",totalNotCanCall=" + totalNotCanCall);
        }
        buf.append("ms");
        return buf.toString();
    }

    public String toXmlString() {
        editCanCall(true);
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        buf.append("\r\n\t\t<call>" + call + "</call>");
        buf.append("<exception>" + exception + "</exception>");
        buf.append("<canCall>" + canCall + "</canCall>");
        buf.append("\r\n\t\t<totalTime>" + (System.currentTimeMillis() - startTotalTime) + "</totalTime>");
        buf.append("<exceptionStartTime>" + exceptionStartTime + "</exceptionStartTime>");
        buf.append("\r\n\t\t<totalCall>" + totalCall + "</totalCall>");
        buf.append("<totalException>" + totalException + "</totalException>");
        if (exceptionStartTime > 0L) {
            buf.append("<totalNotCanCall>" + (totalNotCanCall + (System.currentTimeMillis() - exceptionStartTime))
                    + "</totalNotCanCall>");
        } else {
            buf.append("<totalNotCanCall>" + totalNotCanCall + "</totalNotCanCall>");
        }
        buf.append("<minNumberOfSamples>" + minNumberOfSamples + "</minNumberOfSamples>");
        buf.append("<inspection_cycle>" + inspection_cycle + "</inspection_cycle>");
        buf.append("<exception_threshold>" + exception_threshold + "</exception_threshold>");

        return buf.toString();
    }

    /**
     * @return the alreadyWarn
     */
    public boolean isAlreadyWarn() {
        return alreadyWarn;
    }

    /**
     * @param alreadyWarn the alreadyWarn to set
     */
    public void setAlreadyWarn(boolean alreadyWarn) {
        this.alreadyWarn = alreadyWarn;
    }
}
