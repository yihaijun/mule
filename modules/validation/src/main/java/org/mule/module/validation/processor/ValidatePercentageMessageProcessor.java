/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.PercentValidator;

/**
 * If the specified <code>percentage</code> is not a valid one throw an exception.
 */
public class ValidatePercentageMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Percentage to validate
     */
    private String percentage;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedPercentage = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.percentage);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                BigDecimalValidator validator = PercentValidator.getInstance();
                return validator.isValid(evaluatedPercentage);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("%s is not a valid percentage", evaluatedPercentage);
            }
        };
    }

    public void setPercentage(String percentage)
    {
        this.percentage = percentage;
    }
}
