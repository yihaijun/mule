/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

/**
 * If the specified <code>value</code> is not within the valid limits throw an
 * exception.
 */
public class ValidateLengthMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * String to validate
     */
    private String value;

    /**
     * the minimum length. Defaults to zero
     */
    private String minLength = "0";

    /**
     * the maximum length
     */
    private String maxLength;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedvalue = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.value);

        final Integer evaluatedMinLength = (Integer) this.evaluateAndTransform(this.muleContext, event,
            Integer.class, null, this.minLength, 0);

        final Integer evaluatedMaxLength = (Integer) this.evaluateAndTransform(this.muleContext, event,
            Integer.class, null, this.maxLength);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                int inputLength = evaluatedvalue.length();
                return inputLength >= evaluatedMinLength && inputLength <= evaluatedMaxLength;
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format(
                    "value '%s' was expected to have a length between %d and %d. However, actual length was %d",
                    evaluatedvalue, evaluatedMinLength, evaluatedMaxLength, evaluatedvalue.length());
            }
        };
    }

    public void setValue(String input)
    {
        this.value = input;
    }

    public void setMinLength(String minValue)
    {
        this.minLength = minValue;
    }

    public void setMaxLength(String maxValue)
    {
        this.maxLength = maxValue;
    }

}
