/**
 * 
 */
package com.alibaba.dubbo.bpm;

import java.util.Map;

/**
 * @author yihaijun
 *
 */
public interface MessageService {
    public DubboBpmMessage  generateMessage(String endpoint, Object payloadObject, Map messageProperties, MessageExchangePattern mep) throws Exception;
}
