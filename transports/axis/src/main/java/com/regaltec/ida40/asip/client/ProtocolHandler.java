package com.regaltec.ida40.asip.client;

import com.regaltec.asip.manager.api.client.AsipClientConfigItem;

/**
 * 
 * <p>接口平台协议处理接口</p>
 * <p>创建日期：2010-9-25 下午07:05:21</p>
 *
 * @author 封加华
 */
public interface ProtocolHandler {

    /**
     * <p>协议处理函数，实现者要求不允许抛出任何运行时异常，并且ResponseMessage永远不为空。</p>
     * <p>所有错误信息使用ResponseMessage的errorCode和errorInfo包装,具体的错误码请参照接口平台设计文档。</p>
     * @param context 请求上下文
     * @return 响应消息
     */
    ResponseMessage execute(RequestContext context);

    /**
     * <p>协议处理函数，实现者要求不允许抛出任何运行时异常，并且ResponseMessage永远不为空。</p>
     * <p>所有错误信息使用ResponseMessage的errorCode和errorInfo包装,具体的错误码请参照接口平台设计文档。</p>
     * @param context 请求上下文
     * @return 响应消息
     */
    String execute(String businessServiceName,String businessServiceParam,AsipClientConfigItem asipClientConfig);
}
