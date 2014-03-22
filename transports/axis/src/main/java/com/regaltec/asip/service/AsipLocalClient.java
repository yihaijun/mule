/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import com.regaltec.ida40.asip.client.AsipClient;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-6-14 上午09:52:14</p>
 *
 * @author yihaijun
 */
public class AsipLocalClient extends AsipClient {
    public String call(String businessServiceName, String businessServiceParam, String requestContextName) {
        AsipServiceImpl asipServiceImpl = new AsipServiceImpl();
        return asipServiceImpl.call(businessServiceName, businessServiceParam, requestContextName);
    }
}
