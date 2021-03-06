/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.endpoints;

import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Test configuration of content-type in various endpoints */
public class EndpointContentTypeTestCase  extends FunctionalTestCase
{
    
    @Override
    protected String getConfigResources()
    {
        return "content-type-setting-endpoint-configs.xml";
    }

    @Test
    public void testContentType()  throws Exception
    {
        InboundEndpoint inbound = muleContext.getRegistry().lookupObject("inbound");
        assertEquals("text/xml", inbound.getMimeType());
        assertEquals("utf-8", inbound.getEncoding());
        OutboundEndpoint outbound = muleContext.getRegistry().lookupObject("outbound");
        assertEquals("application/json", outbound.getMimeType());
        assertEquals("iso-8859-2", outbound.getEncoding());
        EndpointBuilder global = muleContext.getRegistry().lookupEndpointBuilder("global");
        InboundEndpoint created = global.buildInboundEndpoint();
        assertEquals("application/xml", created.getMimeType());
        assertEquals("iso-8859-1", created.getEncoding());
    }
}
