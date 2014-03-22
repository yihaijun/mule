/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.common;

import org.apache.log4j.Logger;

/**
 * <p>用于包装日志输出类</p>
 * <p>创建日期：2010-10-8 上午11:40:39</p>
 *
 * @author yihaijun
 */
public class AsipLog4j {
    /**
     * log4j操作句柄
     */
    private Logger log = Logger.getLogger(this.getClass().getName());
    /**用于控制台过虑*/
    private String filter = "(regaltec log,Thread ID=";

    /**
     * 
     * 构造函数
     * @param name category
     */
    public AsipLog4j(String name) {
        log = Logger.getLogger(name);
    }

    /**
     * 
     * 构造函数
     * @param customFilter 用于控制台过虑
     * @param name category
     */
    public AsipLog4j(String customFilter, String name) {
        filter = customFilter;
        log = Logger.getLogger(name);
    }

    /**
     * 
     * <p>同log4j相应函数。</p>
     * @param msg 
     */
    public void trace(final String msg) {
        if (log.isTraceEnabled())
            log.trace(getLogContent(msg));
    }

    /**
     * 
     * <p></p>
     * @param msg 日志内容
     */
    public void debug(final String msg) {
        if (log.isDebugEnabled()) {
            log.debug(getLogContent(msg));
        }
    }

    /**
     * 
     * <p>同log4j相应函数。</p>
     * @param msg 
     */
    public void info(final String msg) {
        log.info(getLogContent(msg));
    }

    /**
     * 
     * <p>同log4j相应函数。</p>
     * @param msg 
     */
    public void warn(final String msg) {
        log.warn(getLogContent(msg));
    }

    /**
     * 
     * <p>同log4j相应函数。</p>
     * @param msg 日志内容
     */
    public void error(final String msg) {
        log.error(getLogContent(msg));
    }

    /**
     * Check whether this category is enabled for the TRACE  Level.
     * @since 1.2.12
     *
     * @return boolean - <code>true</code> if this category is enabled for level
     *         TRACE, <code>false</code> otherwise.
     */
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    /**
     *  Check whether this category is enabled for the <code>DEBUG</code>
     *  Level.
     *
     *  <p> This function is intended to lessen the computational cost of
     *  disabled log debug statements.
     *
     *  <p> For some <code>cat</code> Category object, when you write,
     *  <pre>
     *      cat.debug("This is entry number: " + i );
     *  </pre>
     *
     *  <p>You incur the cost constructing the message, concatenatiion in
     *  this case, regardless of whether the message is logged or not.
     *
     *  <p>If you are worried about speed, then you should write
     *  <pre>
     *    if(cat.isDebugEnabled()) {
     *      cat.debug("This is entry number: " + i );
     *    }
     *  </pre>
     *
     *  <p>This way you will not incur the cost of parameter
     *  construction if debugging is disabled for <code>cat</code>. On
     *  the other hand, if the <code>cat</code> is debug enabled, you
     *  will incur the cost of evaluating whether the category is debug
     *  enabled twice. Once in <code>isDebugEnabled</code> and once in
     *  the <code>debug</code>.  This is an insignificant overhead
     *  since evaluating a category takes about 1%% of the time it
     *  takes to actually log.
     *
     *  @return boolean - <code>true</code> if this category is debug
     *  enabled, <code>false</code> otherwise.
     *   */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * 
     * <p>生成日志最后输出内容。</p>
     * @param msg 日志内容
     * @return    日志最后输出内容 
     */
    private String getLogContent(final String msg) {
        StringBuffer buf = new StringBuffer();
        buf.delete(0, buf.length());
        buf.append(msg);
        buf.append(filter);
        buf.append(Thread.currentThread().getId());
        buf.append(")");
        return buf.toString();
    }
}
