/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.sftp;

import org.mule.api.MuleException;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.module.client.MuleClient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SftpEndpointTestCase extends AbstractSftpTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "mule-sftp-endpoint-config.xml";
    }

    /*
     * For general guidelines on writing transports see
     * http://mule.mulesource.org/display/MULE/Writing+Transports
     */
    @Test
    public void testValidEndpointURI() throws Exception
    {
        // TODO test creating and asserting Endpoint values eg

        EndpointURI url = new MuleEndpointURI("sftp://ms/data", muleContext);
        assertEquals("sftp", url.getScheme());
        assertEquals("ms", url.getHost());
        assertEquals(0, url.getParams().size());
        assertEquals("/data", url.getPath());

    }

    @Test
    public void testValidEndpointURIWithUserAndPasswd() throws Exception
    {
        EndpointURI url = new MuleEndpointURI("sftp://user1:passwd1@localhost:4242/data2", muleContext);
        assertEquals("sftp", url.getScheme());
        assertEquals("localhost", url.getHost());
        assertEquals(4242, url.getPort());
        assertEquals("passwd1", url.getPassword());
        assertEquals("user1", url.getUser());

        assertEquals(0, url.getParams().size());

    }

    @Test
    public void testEndpointConfig() throws MuleException
    {
        MuleClient muleClient = new MuleClient(muleContext);
        ImmutableEndpoint endpoint1 = (ImmutableEndpoint) muleClient.getProperty("inboundEndpoint1");

        EndpointURI url1 = endpoint1.getEndpointURI();
        assertEquals("sftp", url1.getScheme());
        assertEquals("foobar-host", url1.getHost());
        assertEquals(4243, url1.getPort());
        assertEquals("passw0rd", url1.getPassword());
        assertEquals("user42", url1.getUser());

        assertEquals("sftp://user42:passw0rd@foobar-host:4243/data", url1.getUri().toString());

        // Verify that both endpoints in the config are equal
        ImmutableEndpoint endpoint2 = (ImmutableEndpoint) muleClient.getProperty("inboundEndpoint2");
        EndpointURI url2 = endpoint2.getEndpointURI();

        assertEquals("sftp", url2.getScheme());
        assertEquals("foobar-host", url2.getHost());
        assertEquals(4243, url2.getPort());
        assertEquals("passw0rd", url2.getPassword());
        assertEquals("user42", url2.getUser());

        assertEquals(url1.getUri().toString(), url2.getUri().toString());

        ImmutableEndpoint outboundEndpoint1 = (ImmutableEndpoint) muleClient.getProperty("outboundEndpoint1");
        ImmutableEndpoint outboundEndpoint2 = (ImmutableEndpoint) muleClient.getProperty("outboundEndpoint2");

        SftpUtil oUtil1 = new SftpUtil(outboundEndpoint1);
        SftpUtil oUtil2 = new SftpUtil(outboundEndpoint2);

        assertTrue("'keepFileOnError' should be on by default", oUtil1.isKeepFileOnError());
        assertFalse("'keepFileOnError' should be false", oUtil2.isKeepFileOnError());
    }

}
