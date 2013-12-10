/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.module.validation.Locale;

import org.apache.commons.validator.routines.IntegerValidator;

/**
 * If the specified <code>value</code> is not a valid {@link Integer} throw an
 * exception.
 */
public class ValidateIntegerMessageProcessor extends AbstractNumberValidationMessageProcessor
{

    @Override
    protected Class<? extends Number> getNumberType()
    {
        return Integer.class;
    }

    @Override
    protected Number validateWithoutPattern(String value, Locale locale)
    {
        return IntegerValidator.getInstance().validate(value, locale.getJavaLocale());
    }

    @Override
    protected Number validateWithPattern(String value, String pattern, Locale locale)
    {
        return IntegerValidator.getInstance().validate(value, pattern, locale.getJavaLocale());
    }
}
