/**
 * 
 */
package com.alibaba.dubbo.bpm;

import java.util.Map;

import com.alibaba.dubbo.bpm.api.DubboBpmMessage;
import com.alibaba.dubbo.bpm.api.MessageExchangePattern;

/**
 * @author yihaijun
 *
 */
public interface MessageService {
    public DubboBpmMessage  generateMessage(String endpoint, Object payloadObject, Map messageProperties, MessageExchangePattern mep) throws Exception;
}
