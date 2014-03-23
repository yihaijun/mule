/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.alibaba.dubbo.jbpm;


import java.util.Map;

import com.alibaba.dubbo.bpm.MessageService;
import com.alibaba.dubbo.bpm.ProcessComponent;
import com.alibaba.dubbo.bpm.api.DubboBpmMessage;
import com.alibaba.dubbo.bpm.api.MessageExchangePattern;

/**
 * Proxy for the message-generation service provided by Mule. The real service gets
 * injected here by the {@link ProcessComponent}.
 */
public class DubboJbpmMessageService implements MessageService
{
    private MessageService messageService;

    public void setMessageService(MessageService messageService)
    {
        this.messageService = messageService;
    }

    public DubboBpmMessage generateMessage(String endpoint,
                                       Object payloadObject,
                                       Map messageProperties,
                                       MessageExchangePattern mep) throws Exception
    {
        return messageService.generateMessage(endpoint, payloadObject, messageProperties, mep);
    }
}
