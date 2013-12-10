/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import org.mule.api.MuleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EmptyValidationTestCase extends AbstractValidationModuleTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "validation-empty-test-config.xml";
    }

    @Test
    public void emptyString() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", "");
        this.validate("emptyString", event);
    }

    @Test
    public void emptyCollection() throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("value", new ArrayList<String>());
        this.validate("emptyCollection", event);
    }

    @Test
    public void emptyMap() throws Exception
    {
        Map<String, String> notEmpty = new HashMap<String, String>();
        notEmpty.put("a", "b");
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("notEmpty", notEmpty);
        event.setFlowVariable("emptyMap", new HashMap<String, String>());
        this.validate("emptyMap", event);
    }
}
