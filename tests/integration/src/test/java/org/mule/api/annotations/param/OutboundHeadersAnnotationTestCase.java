/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.annotations.param;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OutboundHeadersAnnotationTestCase extends FunctionalTestCase
{
    public OutboundHeadersAnnotationTestCase()
    {
        setDisposeContextPerClass(true);
    }

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/annotations/outbound-headers-annotation.xml";
    }

    @Test
    public void testProcessHeader() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage message = client.send("vm://header", null, null);
        assertNotNull("return message from MuleClient.send() should not be null", message);
        assertTrue("Message payload should be a Map", message.getPayload() instanceof Map);
        Map<?, ?> result = (Map<?, ?>) message.getPayload();
        assertEquals("barValue", result.get("bar"));
    }

    @Test
    public void testProcessHeaderWithExistingOutHeaders() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage message = client.send("vm://header2", null, null);
        assertNotNull("return message from MuleClient.send() should not be null", message);
        assertTrue("Message payload should be a Map", message.getPayload() instanceof Map);
        Map<?, ?> result = (Map<?, ?>) message.getPayload();
        assertEquals("barValue", result.get("bar"));
        assertEquals("fooValue", result.get("foo"));
    }

    @Test
    public void testInvalidParamType() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage message = client.send("vm://invalid", null, null);
        assertNotNull("return message from MuleClient.send() should not be null", message);
        assertNotNull(message.getExceptionPayload());
        assertTrue(message.getExceptionPayload().getRootException() instanceof IllegalArgumentException);
    }
}
