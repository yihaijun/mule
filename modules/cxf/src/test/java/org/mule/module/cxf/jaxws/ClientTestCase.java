/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.cxf.jaxws;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import java.util.HashMap;
import java.util.Map;

import org.apache.hello_world_soap_http.GreeterImpl;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTestCase extends FunctionalTestCase
{

    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");

    @Override
    protected String getConfigResources()
    {
        return "jaxws-client-conf.xml";
    }

    @Test
    public void testGeneratedClientWithQuartz() throws Exception
    {
        GreeterImpl impl = getGreeter();

        Thread.sleep(5000);

        assertEquals(1, impl.getInvocationCount());
    }

    @Test
    public void testClientWithMuleClient() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("operation", "greetMe");
        MuleMessage result = client.send("clientEndpoint", "Dan", props);
        assertEquals("Hello Dan", result.getPayload());

        GreeterImpl impl = getGreeter();

        Thread.sleep(5000);

        assertEquals(2, impl.getInvocationCount());
    }

    private GreeterImpl getGreeter() throws Exception
    {
        Object instance = getComponent("greeterService");

        return (GreeterImpl) instance;
    }

}
