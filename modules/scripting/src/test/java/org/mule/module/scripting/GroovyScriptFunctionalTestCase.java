/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.scripting;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GroovyScriptFunctionalTestCase extends FunctionalTestCase
{
    
    public GroovyScriptFunctionalTestCase()
    {
        //Groovy really hammers the startup time since it needs to create the interpreter on every start
        setDisposeContextPerClass(true);
    }

    @Override
    protected String getConfigResources()
    {
        return "groovy-component-config.xml";
    }

    @Test
    public void testInlineScript() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://in1", "Important Message", null);
        MuleMessage response = client.request("vm://out1", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertEquals("Important Message Received", response.getPayloadAsString());
    }
    
    @Test
    public void testFileBasedScript() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://in2", "Important Message", null);
        MuleMessage response = client.request("vm://out2", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertEquals("Important Message Received", response.getPayloadAsString());
    }
    
    @Test
    public void testReferencedScript() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://in3", "Important Message", null);
        MuleMessage response = client.request("vm://out3", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertEquals("Important Message Received", response.getPayloadAsString());
    }    

    @Test
    public void testScriptVariables() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://in4", "Important Message", null);
        MuleMessage response = client.request("vm://out4", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertEquals("Important Message Received A-OK", response.getPayloadAsString());
    }    
}


