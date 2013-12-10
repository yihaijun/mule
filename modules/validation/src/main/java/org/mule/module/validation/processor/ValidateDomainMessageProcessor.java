/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.DomainValidator;

/**
 * If the specified <code>domain</code> does not parses as a valid domain name with a
 * recognized top-level domain then throw an exception.
 */
public class ValidateDomainMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Domain name to validate
     */
    private String domain;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedDomain = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.domain);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                return DomainValidator.getInstance().isValid(evaluatedDomain);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("Domain %s is not valid", evaluatedDomain);
            }
        };
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }
}
