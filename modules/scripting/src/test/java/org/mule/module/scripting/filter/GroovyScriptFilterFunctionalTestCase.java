/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.scripting.filter;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GroovyScriptFilterFunctionalTestCase extends FunctionalTestCase
{

    public GroovyScriptFilterFunctionalTestCase()
    {
        // Groovy really hammers the startup time since it needs to create the
        // interpreter on every start
        setDisposeContextPerClass(true);
    }

    @Override
    protected String getConfigResources()
    {
        return "groovy-filter-config.xml";
    }

    @Test
    public void testFilterScript() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://in1", "hello", null);
        MuleMessage response = client.request("vm://out1", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertEquals("hello", response.getPayload());

        client.dispatch("vm://in1", "1", null);
        response = client.request("vm://out1", RECEIVE_TIMEOUT);
        assertNull(response);
    }
}
