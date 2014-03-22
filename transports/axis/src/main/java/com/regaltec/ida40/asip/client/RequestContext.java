package com.regaltec.ida40.asip.client;

import java.io.Serializable;

import org.apache.commons.lang.RandomStringUtils;

import com.regaltec.asip.manager.api.client.AsipClientConfigItem;

/**
 * 
 * <p>Asip请求上下文</p>
 * <p>创建日期：2010-9-25 下午07:14:48</p>
 *
 * @author 封加华
 */
public class RequestContext implements Serializable {

    private static final long serialVersionUID = 20110125L;

    private String requestId="";

    private String businessModuleName="";

    private RequestMessage requestMessage=null;

    /**
     * 主配置
     */
    private AsipClientConfigItem asipClientConfigItem;

    /**
     * 
     * 构造函数
     * @param asipConfig 接口平台配置对象
     * @param businessModuleName 业务模块名称
     */
    public RequestContext(final AsipClientConfigItem asipClientConfig, final String businessModuleName) {
        try {
            setRequestId(RandomStringUtils.randomAlphabetic(32));
            this.asipClientConfigItem = asipClientConfig;
            this.businessModuleName = businessModuleName;
            this.requestMessage=new RequestMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the businessModuleName
     */
    public String getBusinessModuleName() {
        return businessModuleName;
    }

    /**
     * @param businessModuleName the businessModuleName to set
     */
    public void setBusinessModuleName(String businessModuleName) {
        this.businessModuleName = businessModuleName;
    }

    /**
     * @return the requestMessage
     */
    public RequestMessage getRequestMessage() {
        return requestMessage;
    }

    /**
     * @param requestMessage the requestMessage to set
     */
    public void setRequestMessage(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }
    /**
     * @return the asipConfig
     */
    public AsipClientConfigItem getAsipClientConfigItem() {
        return asipClientConfigItem;
    }
    /**
     * @param asipConfig the asipConfig to set
     */
    public void setAsipConfigItem(AsipClientConfigItem asipClientConfigItem) {
        this.asipClientConfigItem = asipClientConfigItem;
    }
    
}
