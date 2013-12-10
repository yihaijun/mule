/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import org.mule.api.MuleEvent;

import org.junit.Test;

public class IsbnValidationTestCase extends AbstractValidationModuleTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "validation-isbn-test-config.xml";
    }

    @Test
    public void isbn10() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("isbn", "XX%");
        this.validate("isbn10", event);
    }

    @Test
    public void isbn13() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("isbn", "XX%");
        this.validate("isbn13", event);
    }

}
