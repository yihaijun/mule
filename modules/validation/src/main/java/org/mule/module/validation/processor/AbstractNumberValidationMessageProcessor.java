/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Locale;
import org.mule.module.validation.Validator;

import java.lang.reflect.Type;

/**
 * Base class for validators that test that a String can be parsed into a numeric value
 */
public abstract class AbstractNumberValidationMessageProcessor extends AbstractValidationMessageProcessor
{

    private static final String VALIDATION_NOT_VALID = "VALIDATION_NOT_VALID";
    private static final String VALIDATION_LOWER_BOUNDARY = "VALIDATION_LOWER_BOUNDARY";
    private static final String VALIDATION_UPPER_BOUNDARY = "VALIDATION_UPPER_BOUNDARY";

    /**
     * Value to validate
     */
    private String value;
    
    /**
     * The locale to use for the format
     */
    private String locale;
    
    /**
     * The pattern used to format the value
     */
    private String pattern;
    
    /**
     * The minimum value
     */
    private String minValue;
    
    /**
     * The maximum value
     */
    private String maxValue;

    protected abstract Number validateWithPattern(String value, String pattern, Locale locale);

    protected abstract Number validateWithoutPattern(String value, Locale locale);

    protected abstract Class<? extends Number> getNumberType();

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedPattern = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.pattern);

        final String evaluatedValue = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.value);

        final Type numberType = this.getNumberType();

        final Number evaluatedMinValue = (Number) this.evaluateAndTransform(this.muleContext, event,
            numberType, null, this.minValue);

        final Number evaluatedMaxValue = (Number) this.evaluateAndTransform(this.muleContext, event,
            numberType, null, this.maxValue);

        final Locale evaluatedLocale = (Locale) this.evaluateAndTransform(this.muleContext, event,
            Locale.class, null, this.locale);

        return new Validator()
        {

            private String errorType = null;

            @Override
            public boolean isValid(MuleEvent event)
            {
                Number newValue = null;
                if (evaluatedPattern != null)
                {
                    newValue = validateWithPattern(evaluatedValue, evaluatedPattern, evaluatedLocale);
                }
                else
                {
                    newValue = validateWithoutPattern(evaluatedValue, evaluatedLocale);
                }

                if (newValue == null)
                {
                    this.errorType = VALIDATION_NOT_VALID;
                    return false;
                }

                if (evaluatedMinValue != null)
                {
                    if (newValue.doubleValue() < evaluatedMinValue.doubleValue())
                    {
                        this.errorType = VALIDATION_LOWER_BOUNDARY;
                        return false;
                    }
                }

                if (evaluatedMaxValue != null)
                {
                    if (newValue.doubleValue() > evaluatedMaxValue.doubleValue())
                    {
                        this.errorType = VALIDATION_UPPER_BOUNDARY;
                        return false;
                    }
                }

                return true;
            }

            @Override
            public final String getErrorMessage(MuleEvent event)
            {
                if (VALIDATION_NOT_VALID.equals(this.errorType))
                {
                    return String.format("'%s' is not a valid %s value", evaluatedValue,
                        getNumberType().getName());
                }
                else if (VALIDATION_LOWER_BOUNDARY.equals(this.errorType))
                {
                    return String.format("'%s' is lower that %s", evaluatedValue,
                        evaluatedMinValue.toString());
                }
                else if (VALIDATION_UPPER_BOUNDARY.equals(this.errorType))
                {
                    return String.format("'%s' is greater than %s", evaluatedValue,
                        evaluatedMaxValue.toString());
                }
                else
                {
                    return String.format("Unknown validation error for value '%s' with locale %s",
                        evaluatedValue, locale);
                }
            }
        };
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    public void setMinValue(String minValue)
    {
        this.minValue = minValue;
    }

    public void setMaxValue(String maxValue)
    {
        this.maxValue = maxValue;
    }

}
