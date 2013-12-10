/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;
import org.mule.transport.NullPayload;

/**
 * If the given <code>expression</code> resolves to a value that's <code>null</code>
 * or an instance of {@link NullPayload} throw an Exception
 */
public class ValidateNotNullMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * A mule expression or {@link String} literal
     */
    private String expression;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final Object evaluatedExpression = this.evaluateAndTransform(this.muleContext, event, Object.class,
            null, this.expression);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                return evaluatedExpression != null && !(evaluatedExpression instanceof NullPayload);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return "The value is null";
            }
        };
    }

    public void setExpression(String expression)
    {
        this.expression = expression;
    }

}
