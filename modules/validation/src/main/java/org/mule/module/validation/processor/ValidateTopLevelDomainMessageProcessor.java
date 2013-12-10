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
 * If if the specified <code>topLevelDomain</code> does not matches any IANA-defined
 * top-level domain throw an exception. Leading dots are ignored if present. The
 * search is case-sensitive.
 */
public class ValidateTopLevelDomainMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Domain name to validate
     */
    private String topLevelDomain;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedDomain = (String) this.evaluateAndTransform(muleContext, event, String.class,
            null, this.topLevelDomain);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                return DomainValidator.getInstance().isValidTld(evaluatedDomain);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("%s is not a valid top level domain", evaluatedDomain);
            }
        };
    }

    public void setTopLevelDomain(String topLevelDomain)
    {
        this.topLevelDomain = topLevelDomain;
    }

}
