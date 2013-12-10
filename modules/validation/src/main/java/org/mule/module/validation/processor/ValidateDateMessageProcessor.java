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

import org.apache.commons.validator.routines.DateValidator;

/**
 * If the specified <code>date</code> is not a valid one throw an exception.
 */
public class ValidateDateMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Date to validate
     */
    private String date;

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
        final String evaluatedDate = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.date);

        final String evaluatedPattern = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.pattern);

        final Locale evaluatedLocale = (Locale) this.evaluateAndTransform(this.muleContext, event,
            Locale.class, null, this.locale);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                DateValidator validator = DateValidator.getInstance();

                if (evaluatedPattern != null)
                {
                    return validator.isValid(evaluatedDate, evaluatedPattern, evaluatedLocale.getJavaLocale());
                }
                else
                {
                    return validator.isValid(evaluatedDate, evaluatedLocale.getJavaLocale());
                }
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("The date %s is not valid under the pattern '%s' for the locale %s",
                    evaluatedDate, evaluatedPattern, evaluatedLocale.getJavaLocale());
            }
        };
    }

    public void setDate(String date)
    {
        this.date = date;
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
