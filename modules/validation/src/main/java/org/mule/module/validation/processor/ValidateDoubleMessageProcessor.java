/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.module.validation.Locale;

import org.apache.commons.validator.routines.DoubleValidator;

/**
 * If the specified <code>value</code> is not a valid {@link Double} throw an
 * exception.
 */
public class ValidateDoubleMessageProcessor extends AbstractNumberValidationMessageProcessor
{

    @Override
    protected Class<? extends Number> getNumberType()
    {
        return Double.class;
    }

    @Override
    protected Number validateWithPattern(String value, String pattern, Locale locale)
    {
        return DoubleValidator.getInstance().validate(value, pattern, locale.getJavaLocale());
    }

    @Override
    protected Number validateWithoutPattern(String value, Locale locale)
    {
        return DoubleValidator.getInstance().validate(value, locale.getJavaLocale());
    }
}
