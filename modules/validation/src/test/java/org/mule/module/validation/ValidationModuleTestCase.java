/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import org.mule.api.MuleEvent;

import org.junit.Test;

public class ValidationModuleTestCase extends AbstractValidationModuleTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "validation-test-config.xml";
    }

    @Test
    public void invalidExceptionClass() throws Exception
    {
        this.assertFailure("invalidCustomExceptionClass", getTestEvent(""), IllegalArgumentException.class,
            null);
    }

    @Test
    public void invalidExceptionClassConstructor() throws Exception
    {
        this.assertFailure("invalidExceptionClassConstructor", getTestEvent(""),
            IllegalArgumentException.class, null);
    }

    @Test
    public void defaultExceptionWithCustomMessage() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("customMessage", CUSTOM_EXCEPTION_MESSAGE);
        this.assertFailure("defaultExceptionWithCustomMessage", event, null, CUSTOM_EXCEPTION_MESSAGE);
    }
}
