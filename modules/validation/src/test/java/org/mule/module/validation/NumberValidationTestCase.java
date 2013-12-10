/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import org.mule.api.MuleEvent;

import org.junit.Test;

public class NumberValidationTestCase extends AbstractValidationModuleTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "validation-number-test-config.xml";
    }

    @Test
    public void validateLong() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", "5000");
        event.setFlowVariable("min", "1");
        event.setFlowVariable("max", "100");
        this.validate("long", event);
    }

    @Test
    public void validateDouble() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", "5000");
        event.setFlowVariable("min", "1");
        event.setFlowVariable("max", "100");
        this.validate("double", event);
    }

    @Test
    public void validateFloat() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", "5000");
        event.setFlowVariable("min", "1");
        event.setFlowVariable("max", "100");
        this.validate("float", event);
    }

    @Test
    public void validateInteger() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", "5000");
        event.setFlowVariable("min", "1");
        event.setFlowVariable("max", "100");
        this.validate("integer", event);
    }

    @Test
    public void validateShort() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", "5000");
        event.setFlowVariable("min", "1");
        event.setFlowVariable("max", "100");
        this.validate("short", event);
    }

}
