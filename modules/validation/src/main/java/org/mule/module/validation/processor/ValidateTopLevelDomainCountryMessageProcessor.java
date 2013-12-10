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
 * If if the specified <code>countryCode</code> does not matches any IANA-defined
 * top-level domain throw an exception. Leading dots are ignored if present. The
 * search is case-sensitive.
 */
public class ValidateTopLevelDomainCountryMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Country code to validate
     */
    private String countryCode;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedCountryCode = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.countryCode);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                return DomainValidator.getInstance().isValidCountryCodeTld(evaluatedCountryCode);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("%s is not a valid top level domain country code", evaluatedCountryCode);
            }
        };
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
}
