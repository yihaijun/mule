/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.sftp;

import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.module.client.MuleClient;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SftpTempDirFunctionalTestCase extends AbstractSftpTestCase
{

    private static final String OUTBOUND_ENDPOINT_NAME = "outboundEndpoint";
    private static final String INBOUND_ENDPOINT_NAME = "inboundEndpoint";
    private static final String INBOUND_ENDPOINT_NAME2 = "inboundEndpoint2";
    private static final String OUTBOUND_ENDPOINT_NAME2 = "outboundEndpoint2";
    private static final String TEMP_DIR = "uploading";

    @Override
    protected String getConfigResources()
    {
        return "mule-sftp-temp-dir-config.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();

        initEndpointDirectory(INBOUND_ENDPOINT_NAME);
        initEndpointDirectory(OUTBOUND_ENDPOINT_NAME);
        initEndpointDirectory(INBOUND_ENDPOINT_NAME2);
        initEndpointDirectory(OUTBOUND_ENDPOINT_NAME2);
    }

    @Test
    public void testTempDirInbound() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);

        DispatchParameters p = new DispatchParameters(INBOUND_ENDPOINT_NAME2, OUTBOUND_ENDPOINT_NAME2);
        p.setSftpConnector("sftpCustomConnectorTempDirInbound");
        dispatchAndWaitForDelivery(p);

        // Verify inbound
        SftpClient sftpClientInbound = getSftpClient(muleClient, INBOUND_ENDPOINT_NAME2);
        ImmutableEndpoint endpointInbound = (ImmutableEndpoint) muleClient.getProperty(INBOUND_ENDPOINT_NAME2);
        try
        {
            assertTrue("The temp directory should have been created", tempDirectoryExists(sftpClientInbound,
                muleClient, INBOUND_ENDPOINT_NAME2));
            assertFalse("No file should exist in the temp directory", super.verifyFileExists(
                sftpClientInbound, endpointInbound.getEndpointURI().getPath() + "/uploading", FILE_NAME));
            assertFalse("The file should not exist in the source directory", super.verifyFileExists(
                sftpClientInbound, endpointInbound.getEndpointURI(), FILE_NAME));
        }
        finally
        {
            sftpClientInbound.disconnect();
        }

        // Verify outbound
        SftpClient sftpClientOutbound = getSftpClient(muleClient, OUTBOUND_ENDPOINT_NAME2);
        ImmutableEndpoint endpointOutbound = (ImmutableEndpoint) muleClient.getProperty(OUTBOUND_ENDPOINT_NAME2);
        try
        {
            assertFalse("The temp directory should not have been created", tempDirectoryExists(
                sftpClientOutbound, muleClient, OUTBOUND_ENDPOINT_NAME2));
            assertTrue("The file should exist in the final destination : " + FILE_NAME,
                super.verifyFileExists(sftpClientOutbound, endpointOutbound.getEndpointURI(), FILE_NAME));
        }
        finally
        {
            sftpClientOutbound.disconnect();
        }

    }

    @Test
    public void testTempDirOutbound() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);

        DispatchParameters p = new DispatchParameters(INBOUND_ENDPOINT_NAME, OUTBOUND_ENDPOINT_NAME);
        p.setSftpConnector("sftpCustomConnector");
        dispatchAndWaitForDelivery(p);

        // Verify inbound
        SftpClient sftpClientInbound = getSftpClient(muleClient, INBOUND_ENDPOINT_NAME);
        ImmutableEndpoint endpointInbound = (ImmutableEndpoint) muleClient.getProperty(INBOUND_ENDPOINT_NAME);
        try
        {
            assertFalse("The temp directory should not have been created", tempDirectoryExists(
                sftpClientInbound, muleClient, INBOUND_ENDPOINT_NAME));
            assertFalse("The file should not exist in the source directory", super.verifyFileExists(
                sftpClientInbound, endpointInbound.getEndpointURI(), FILE_NAME));
        }
        finally
        {
            sftpClientInbound.disconnect();
        }

        // Verify outbound
        SftpClient sftpClientOutbound = getSftpClient(muleClient, OUTBOUND_ENDPOINT_NAME);
        ImmutableEndpoint endpointOutbound = (ImmutableEndpoint) muleClient.getProperty(OUTBOUND_ENDPOINT_NAME);
        try
        {
            assertTrue("The temp directory should have been created", tempDirectoryExists(sftpClientOutbound,
                muleClient, OUTBOUND_ENDPOINT_NAME));
            assertTrue("The file should exist in the final destination", super.verifyFileExists(
                sftpClientOutbound, endpointOutbound.getEndpointURI(), FILE_NAME));
        }
        finally
        {
            sftpClientOutbound.disconnect();
        }
    }

    private boolean tempDirectoryExists(SftpClient sftpClient, MuleClient muleClient, String endpointName)
        throws IOException
    {
        try
        {
            EndpointURI endpointURI = getUriByEndpointName(muleClient, endpointName);

            sftpClient.changeWorkingDirectory(endpointURI.getPath() + "/" + TEMP_DIR);
            return true;
        }
        catch (IOException f)
        {
            return false;
        }
    }
}
