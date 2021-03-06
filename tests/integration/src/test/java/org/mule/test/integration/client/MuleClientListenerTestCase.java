/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.client;


import org.mule.api.MuleMessage;
import org.mule.api.service.Service;
import org.mule.api.transport.DispatchException;
import org.mule.api.transport.NoReceiverForEndpointException;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class MuleClientListenerTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/client/mule-client-listener-config.xml";
    }

    public void doTestRegisterListener(String component, String endpoint, boolean canSendWithoutReceiver) throws Exception
    {
        MuleClient client = new MuleClient(muleContext);

        try
        {
            client.send(endpoint, "Test Client Send message", null);
        }
        catch (DispatchException e)
        {
            if (!canSendWithoutReceiver)
            {
                assertTrue(e instanceof DispatchException);
                assertTrue(e.getCause() instanceof NoReceiverForEndpointException);
            }
        }
        
        Service c = muleContext.getRegistry().lookupService(component);
        c.start();

        MuleMessage message = client.send(endpoint, "Test Client Send message", null);
        assertNotNull(message);
        assertEquals("Received: Test Client Send message", message.getPayloadAsString());

        // The SpringRegistry is read-only so we can't unregister the service!
        //muleContext.getRegistry().unregisterComponent("vmComponent");
        c.stop();

        try
        {
            client.send(endpoint, "Test Client Send message", null);
        }
        catch (DispatchException e)
        {
            if (!canSendWithoutReceiver)
            {
                assertTrue(e instanceof DispatchException);
                assertTrue(e.getCause() instanceof NoReceiverForEndpointException);
            }
        }
    }

    @Test
    public void testRegisterListenerVm() throws Exception
    {
        doTestRegisterListener("vmComponent", "vm://test.queue", false);
    }

    @Test
    public void testRegisterListenerTcp() throws Exception
    {
        doTestRegisterListener("tcpComponent", "tcp://localhost:56324", true);
    }

}
