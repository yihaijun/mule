/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.soap.axis;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transport.DispatchException;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.tck.testmodels.services.TestComponent;
import org.mule.tck.testmodels.services.TestComponentException;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AxisExceptionTestCase extends FunctionalTestCase
{

    @Rule
    public DynamicPort dynamicPort1 = new DynamicPort("port1");

    @Rule
    public DynamicPort dynamicPort2 = new DynamicPort("port2");
    
    @Override
    protected String getConfigResources()
    {
        return "axis-using-cxf-config.xml";
    }

    @Test
    public void testSuccessCall() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage reply = client.send("axis:http://localhost:" + dynamicPort1.getNumber() + "/services/AxisService?method=receive",
            new DefaultMuleMessage("test", muleContext));

        assertNotNull(reply);
        assertNotNull(reply.getPayload());
        assertTrue(reply.getPayload() instanceof String);
        assertEquals("Received: test", reply.getPayloadAsString());
    }

    @Test
    public void testExceptionCall() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        try
        {
            client.send("axis:http://localhost:" + dynamicPort1.getNumber() + "/services/AxisService?method=throwsException", new DefaultMuleMessage("test", muleContext));
            fail("should have thrown exception");
        }
        catch (DispatchException e)
        {
            assertEquals(TestComponentException.class.getName() + ": "
                         + TestComponentException.MESSAGE_PREFIX + TestComponent.EXCEPTION_MESSAGE, e.getCause().getMessage());
        }
    }

    @Test
    public void testExceptionBasedRoutingForAxis() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage reply = client.send("vm://localhost.test", new DefaultMuleMessage("test", muleContext));

        assertNotNull(reply);
        assertNotNull(reply.getPayload());
        assertTrue(reply.getPayload() instanceof String);
        assertEquals("Received: test", reply.getPayloadAsString());
    }

}
