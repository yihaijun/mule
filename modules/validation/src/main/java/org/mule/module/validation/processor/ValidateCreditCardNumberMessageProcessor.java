/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.module.validation.CreditCardType;
import org.mule.module.validation.Validator;

import org.apache.commons.validator.routines.CodeValidator;
import org.apache.commons.validator.routines.CreditCardValidator;

/**
 * If if the specified <code>creditCardNumber</code> is not a valid credit card
 * number throw an exception.
 */
public class ValidateCreditCardNumberMessageProcessor extends AbstractValidationMessageProcessor
{

    /**
     * Credit card number to validate
     */
    private String creditCardNumber;

    /**
     * Credit card type to validate
     */
    private String creditCardType;

    @Override
    protected Validator getValidator(MuleEvent event) throws Exception
    {
        final String evaluatedCreditCardNumber = (String) this.evaluateAndTransform(this.muleContext, event,
            String.class, null, this.creditCardNumber);

        final CreditCardType evaluatedCreditCardType = (CreditCardType) this.evaluateAndTransform(
            this.muleContext, event, CreditCardType.class, null, this.creditCardType);

        return new Validator()
        {

            @Override
            public boolean isValid(MuleEvent event)
            {
                CreditCardValidator validator = new CreditCardValidator(
                    new CodeValidator[]{evaluatedCreditCardType.getCodeValidator()});
                return validator.validate(evaluatedCreditCardNumber) != null;
            }

            @Override
            public String getErrorMessage(MuleEvent event)
            {
                return String.format("Credit card number %s is not valid for card type: %s",
                    evaluatedCreditCardNumber, evaluatedCreditCardType);
            }
        };
    }

    public void setCreditCardNumber(String creditCardNumber)
    {
        this.creditCardNumber = creditCardNumber;
    }

    public void setCreditCardType(String creditCardType)
    {
        this.creditCardType = creditCardType;
    }
}
