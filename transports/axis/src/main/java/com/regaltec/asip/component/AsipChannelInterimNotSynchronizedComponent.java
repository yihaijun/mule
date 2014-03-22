/*
 * <p>标题: 中国电信综合调度系统第4版</p>
 * <p>描述: ...</p>
 * <p>版权: Copyright (c) 2012</p>
 * <p>公司: 天讯瑞达通信技术有限公司</p>
 * <p>网址：http://www.tisson.cn/</p>
 */
package com.regaltec.asip.component;

import java.util.Properties;

import org.mule.api.MuleEvent;
import org.mule.component.AbstractComponent;

import com.regaltec.asip.utils.PropertiesMapping;

/**
 * <p>概述该类作用，请详细描述。</p>
 * <p>创建日期：2012-8-21 下午04:59:15</p>
 *
 * @author yihaijun
 */
public class AsipChannelInterimNotSynchronizedComponent extends AbstractComponent {
    @Override
    protected Object doInvoke(MuleEvent event) throws Exception {
        String channelName = "UNKNOW";
        try {
            String channel = event.getMessage().getInboundProperty("ASIP_INTERIMAYNCHRONIZED_CHANNEL");
            channelName = event.getMessage().getInboundProperty("ASIP_INTERIMAYNCHRONIZED_CHANNEL_NAME");
            event.getMuleContext().getClient().dispatch(channel, event.getMessage());
        } catch (Exception e) {
            // e.printStackTrace();
        }
        String asipName = "ASIP";
        Properties t_asip_service_element_list =
                new PropertiesMapping("asipconf/t_asip_service_element_list.properties").getProperties();
        if (t_asip_service_element_list != null && t_asip_service_element_list.size() > 0) {
            asipName = t_asip_service_element_list.getProperty("asip_service_name", asipName);
        }

        String result =
                com.regaltec.asip.utils.IdaMsgUtils.getAsipNullRetun("ASIP-2036", channelName + "通道异常," + asipName
                        + "放弃调用此系统服务", 0, 0, 0);
        return result;
    }
}
