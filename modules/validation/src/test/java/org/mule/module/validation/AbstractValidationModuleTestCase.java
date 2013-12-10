/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.module.validation.exception.ValidationException;
import org.mule.tck.junit4.FunctionalTestCase;

public abstract class AbstractValidationModuleTestCase extends FunctionalTestCase
{

    protected static final String CUSTOM_EXCEPTION_MESSAGE = "failed!";

    protected void validate(String flowName, MuleEvent event) throws Exception
    {
        this.testFlow(flowName + "Valid", event);
        this.assertFailure(flowName + "Invalid", event, null, null);
        this.assertFailure(flowName + "CustomInvalid", event, ValidationTestException.class,
            CUSTOM_EXCEPTION_MESSAGE);
    }

    protected void assertFailure(String flowName,
                                 MuleEvent event,
                                 Class<? extends Exception> exceptionClass,
                                 String message) throws Exception
    {
        if (exceptionClass != null)
        {
            event.setFlowVariable("exceptionClass", exceptionClass);
        }
        else
        {
            exceptionClass = ValidationException.class;
        }

        if (message != null)
        {
            event.setFlowVariable("customMessage", message);
        }

        try
        {
            this.testFlow(flowName, event);
            fail("was expecting a validation error");
        }
        catch (MessagingException e)
        {
            Exception cause = e.getCauseException();
            assertEquals(exceptionClass, cause.getClass());
            if (message != null)
            {
                assertEquals(message, cause.getMessage());
            }
        }
    }
}
