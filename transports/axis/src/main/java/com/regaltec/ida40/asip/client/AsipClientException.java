package com.regaltec.ida40.asip.client;

/**
 * <p>调用AsipClient发生异常</p>
 * <p>创建日期：2010-9-3 下午03:03:53</p>
 *
 * @author 封加华
 * @author 朱志欧
 */
public class AsipClientException extends Exception {

    private static final long serialVersionUID = 20110402034729L;

    private String asipCode;

    private String asipInfo;

    /**
     * 
     * 构造函数
     * @param message 异常消息
     * @param cause 基异常 
     */
    public AsipClientException(String asipCode, String asipInfo, Throwable cause) {
        super(asipInfo, cause);
        this.asipCode = asipCode;
        this.asipInfo = asipInfo;
    }
    /**
     * 
     * 构造函数
     * @param message 异常消息
     */
    public AsipClientException(String asipCode, String asipInfo) {
        super(asipInfo);
        this.asipCode = asipCode;
        this.asipInfo = asipInfo;
    }

    /**
     * @return the asipCode
     */
    public String getAsipCode() {
        return asipCode;
    }

    /**
     * @return the asipInfo
     */
    public String getAsipInfo() {
        return asipInfo;
    }
}
