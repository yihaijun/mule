/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2013</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.service;

import com.regaltec.asip.manager.api.client.AsipReqeustParamter;
import com.regaltec.ida40.asip.client.AsipClient;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2013-6-14 上午11:42:55</p>
 *
 * @author yihaijun
 */
public class SampleAsipReqeustParamterToAsipService {
    private AsipClient asipClient;

    /**
     * @return the asipClient
     */
    public AsipClient getAsipClient() {
        return asipClient;
    }


    /**
     * @param asipClient the asipClient to set
     */
    public void setAsipClient(AsipClient asipClient) {
        this.asipClient = asipClient;
    }


    public String gateway(AsipReqeustParamter asipReqeustParamter) {
        return asipClient.call("SampleAsipReqeustParamterToAsipService", asipReqeustParamter.toText(), null);
    }
}
