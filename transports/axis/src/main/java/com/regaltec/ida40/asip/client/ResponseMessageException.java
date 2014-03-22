/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2011</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.ida40.asip.client;

/**
 * <p>接口平台响应消息的异常。</p>
 * <p>创建日期：2011-4-02 15:58:59</p>
 *
 * @author 朱志欧
 */
public class ResponseMessageException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     */
    public ResponseMessageException() {
    }

    /**
     * 构造函数
     * @param message
     * @param cause
     */
    public ResponseMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     * @param message
     */
    public ResponseMessageException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * @param cause
     */
    public ResponseMessageException(Throwable cause) {
        super(cause);
    }

}
