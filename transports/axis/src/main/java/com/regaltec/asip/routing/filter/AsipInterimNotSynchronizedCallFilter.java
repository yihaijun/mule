/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.routing.filter;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import com.regaltec.asip.service.AsipServiceInterimNotSynchronizedCall;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-8-21 上午09:06:44</p>
 *
 * @author yihaijun
 */
public class AsipInterimNotSynchronizedCallFilter implements Filter {
    private String channel;

    @Override
    public boolean accept(MuleMessage message) {
        return  !new AsipServiceInterimNotSynchronizedCall().isCanCall(channel);
    }

    /**
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }
}