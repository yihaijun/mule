/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.cxf;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.http.HttpConstants;

import java.util.HashMap;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Http10TestCase extends FunctionalTestCase
{

    @ClassRule
    public static DynamicPort dynamicPort = new DynamicPort("port1");

    public Http10TestCase()
    {
        setDisposeContextPerClass(true);
    }

    @Override
    protected String getConfigResources()
    {
        return "http-10-conf.xml";
    }

    @Test
    public void testHttp10TransformerNotOnProtocol() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String,String> props = new HashMap<String, String>();
        
        MuleMessage result = client.send("cxfOutbound", "Dan", props);
        assertEquals("Hello Dan", result.getPayload());
        
        result = client.request("vm://out", 1000);
        assertFalse("chunked".equals(result.getOutboundProperty(HttpConstants.HEADER_TRANSFER_ENCODING)));
    }

    @Test
    public void testHttp10TransformerOnProtocol() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String,String> props = new HashMap<String, String>();
        
        MuleMessage result = client.send("cxfOutbound2", "Dan", props);
        assertEquals("Hello Dan", result.getPayload());
        
        result = client.request("vm://out", 1000);
        assertFalse("chunked".equals(result.getOutboundProperty(HttpConstants.HEADER_TRANSFER_ENCODING)));
    }
}
