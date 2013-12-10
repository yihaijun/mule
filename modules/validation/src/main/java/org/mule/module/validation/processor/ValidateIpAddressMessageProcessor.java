/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * If the specified <code>ip</code> is not a valid one throw an exception.
 */
public class ValidateIpAddressMessageProcessor extends AbstractValidationMessageProcessor
{
    /**
     * IP address to validate
     */
    private String ip;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {

        final String evaluatedIp = (String) this.evaluateAndTransform(this.muleContext, event, String.class,
            null, this.ip);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                return InetAddressValidator.getInstance().isValid(evaluatedIp);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("'%s' is not a valid ip address", evaluatedIp);
            }
        };
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }
}
