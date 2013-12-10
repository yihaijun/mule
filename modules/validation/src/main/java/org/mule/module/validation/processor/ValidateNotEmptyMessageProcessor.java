/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;
import org.mule.mvel2.compiler.BlankLiteral;
import org.mule.transport.NullPayload;
import org.mule.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * If the specified <code>value</code> is empty or null throw an exception. Value can
 * be a {@link String} literal or an expression which resolves to a {@link String},
 * {@link Collection} or {@link Map}
 */
public class ValidateNotEmptyMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * A {@link String} literal or an expression which resolves to a {@link String},
     * {@link Collection} or {@link Map}
     */
    private String value;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final Object evaluatedValue = this.evaluateAndTransform(this.muleContext, event, Object.class, null,
            this.value);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                if (evaluatedValue == null || evaluatedValue instanceof NullPayload)
                {
                    return false;
                }
                else if (evaluatedValue instanceof Collection)
                {
                    return !((Collection<?>) evaluatedValue).isEmpty();
                }
                else if (evaluatedValue instanceof String)
                {
                    return !StringUtils.isBlank((String) evaluatedValue);
                }
                else if (evaluatedValue instanceof Map)
                {
                    return !((Map<?, ?>) evaluatedValue).isEmpty();
                }
                else if (evaluatedValue instanceof BlankLiteral)
                {
                    return false;
                }

                throw new IllegalArgumentException(
                    String.format(
                        "Only instances of Map, Collection and String can be checked for emptyness. Instance of %s was found instead",
                        evaluatedValue.getClass().getCanonicalName()));
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return "value was empty";
            }
        };
    }

    public void setValue(String expression)
    {
        this.value = expression;
    }
}
