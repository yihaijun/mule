/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.ISBNValidator;

/**
 * If the specified <code>isbn</code> is not a valid ISBN10 code throw an exception.
 */
public class ValidateISBN10MessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * ISBN10 code to validate
     */
    private String isbn;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedIsbn = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.isbn);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                return ISBNValidator.getInstance().isValidISBN10(evaluatedIsbn);
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("%s is not a valid ISBN10 code", evaluatedIsbn);
            }
        };
    }

    public void setIsbn(String isbnCode)
    {
        this.isbn = isbnCode;
    }
}
