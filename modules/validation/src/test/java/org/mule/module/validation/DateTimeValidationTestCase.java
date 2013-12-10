/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import org.mule.api.MuleEvent;

import org.junit.Test;

public class DateTimeValidationTestCase extends AbstractValidationModuleTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "validation-datetime-test-config.xml";
    }

    @Test
    public void date() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("date", "01/01");
        event.setFlowVariable("pattern", "yyyy.MM.dd G 'at' HH:mm:ss z");
        event.setFlowVariable("locale", "US");
        this.validate("date", event);
    }

    @Test
    public void time() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("time", "6:38PM");
        this.validate("time", event);
    }

}
