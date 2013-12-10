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

import org.apache.commons.validator.routines.TimeValidator;

/**
 * If the specified <code>time</code> is not a valid one throw an exception.
 */
public class ValidateTimeMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Time to validate
     */
    private String time;

    /**
     * The locale to use for the format
     */
    private String locale;

    /**
     * The pattern used to format the value
     */
    private String pattern;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedTime = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.time);

        final String evaluatedPattern = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.pattern);

        final Locale evaluatedLocale = (Locale) this.evaluateAndTransform(this.muleContext, event,
            Locale.class, null, this.locale);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                TimeValidator validator = TimeValidator.getInstance();

                if (evaluatedPattern != null)
                {
                    if (!validator.isValid(evaluatedTime, evaluatedPattern, evaluatedLocale.getJavaLocale()))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!validator.isValid(evaluatedTime, evaluatedLocale.getJavaLocale()))
                    {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("%s is not a valid time for the pattern %s under locale %s",
                    evaluatedTime, evaluatedPattern, evaluatedLocale);
            }
        };
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }
}
