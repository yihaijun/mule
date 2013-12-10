/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.RegexValidator;

/**
 * If the specified <code>value</code> does not match the regex then throw an
 * exception.
 */
public class ValidateUsingRegexMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Value to match
     */
    private String value;

    /**
     * regular expression to test against
     */
    private String regex;

    /**
     * when <code>true</code> matching is case sensitive, otherwise matching is case
     * in-sensitive
     */
    private String caseSensitive;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedValue = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.value);

        final String evalutedRegex = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.regex);

        final boolean evaluatedCaseSensitive = (Boolean) this.evaluateAndTransform(this.muleContext, event,
            Boolean.class, null, this.caseSensitive, false);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                RegexValidator validator = new RegexValidator(new String[]{evalutedRegex},
                    evaluatedCaseSensitive);
                return validator.isValid(evaluatedValue);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("Value %s is not valid under the terms of regex %s", evaluatedValue,
                    evalutedRegex);
            }
        };
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    public void setCaseSensitive(String caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }

}
