package com.regaltec.ida40.asip.client;

import com.regaltec.asip.manager.api.client.AsipClientConfigItem;

/**
 * 
 * <p>接口平台客户端Tcp协议实现</p>
 * <p>创建日期：2010-9-25 下午10:22:15</p>
 *
 * @author 封加华
 */
public class TCPProtocolHandler extends AbstractProtocolHandler {

    /**
     * 
     * {@inheritDoc}
     */
    public ResponseMessage execute(RequestContext context) {
        throw new UnsupportedOperationException("没有实现接口平台tcp协议");
    }

    public String execute(String businessServiceName, String businessServiceParam, AsipClientConfigItem asipClientConfig) {
        return null;
    }

}
