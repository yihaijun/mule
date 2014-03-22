/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2010</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/
 */
package com.regaltec.asip.service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * <p>asip向综调发布的服务</p>
 * <p>创建日期：2010-9-19 下午06:24:15</p>
 *
 * @author yihaijun
 */
@WebService(serviceName = "AsipService", portName = "AsipService")
public interface AsipService {
    /**
         * <p>asip向综调发布的服务</p>
         * @param businessServiceName 指定业务服务名称
         * @param businessServiceParam 服务请求参数组
         * @param requestContextName 一般填空,但在需使用同一个类实例多个函数配合时就要使用
         * @return xml字符串，它和businessServiceParam 格式相同
         */
    @WebResult(name = "return")
    public String call(@WebParam(name = "businessServiceName") String businessServiceName,
            @WebParam(name = "businessServiceParam") String businessServiceParam,
            @WebParam(name = "requestContextName") String requestContextName);
}