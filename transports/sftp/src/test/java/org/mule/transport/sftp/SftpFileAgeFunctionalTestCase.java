/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.sftp;

import org.mule.api.MuleEventContext;
import org.mule.module.client.MuleClient;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.functional.FunctionalTestComponent;

import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * <code>SftpFileAgeFunctionalTestCase</code> tests the fileAge functionality.
 * 
 * @author Lennart Häggkvist
 */

public class SftpFileAgeFunctionalTestCase extends AbstractSftpTestCase
{
    
    private static final Log logger = LogFactory.getLog(SftpFileAgeFunctionalTestCase.class);

    private static final String INBOUND_ENDPOINT_NAME = "inboundEndpoint";

    protected static final long TIMEOUT = 10000 * 6;

    @Override
    protected String getConfigResources()
    {
        return "mule-sftp-file-age-config.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();

        initEndpointDirectory("inboundEndpoint");
    }

    @Test
    public void testFileAge() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference message = new AtomicReference();
        final AtomicInteger loopCount = new AtomicInteger(0);

        EventCallback callback = new EventCallback()
        {
            public synchronized void eventReceived(MuleEventContext context, Object component)
            {
                try
                {
                    logger.info("called " + loopCount.incrementAndGet() + " times");
                    // without this we may have problems with the many repeats
                    if (1 == latch.getCount())
                    {
                        String o = IOUtils.toString((SftpInputStream) context.getMessage().getPayload());
                        message.set(o);
                        latch.countDown();
                    }
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        };

        MuleClient client = new MuleClient(muleContext);

        // Ensure that no other files exists
        // cleanupRemoteFtpDirectory(client, INBOUND_ENDPOINT_NAME);

        Object component = getComponent("testComponent");
        assertTrue("FunctionalTestComponent expected", component instanceof FunctionalTestComponent);
        FunctionalTestComponent ftc = (FunctionalTestComponent) component;
        assertNotNull(ftc);

        ftc.setEventCallback(callback);

        // Use one specific filename so that the file is overwritten if necessarily
        Map properties = new HashMap();
        // properties.put("filename", "fileage-test.tmp");

        long startTime = System.currentTimeMillis();

        logger.debug("before dispatch");
        // Send an file to the SFTP server, which the inbound-endpoint then can pick
        // up
        client.dispatch(getAddressByEndpoint(client, INBOUND_ENDPOINT_NAME), TEST_MESSAGE, properties);
        logger.debug("before retrieve");

        latch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        // We assume that the total time never should be less than fileAge. That
        // means that the fileAge value
        // in this test must be rather high
        long time = System.currentTimeMillis() - startTime;

        int maxTimeDiff = 1000; // Max time diff between localhost and the server.
                                // Ie. the time can differ up to this and the test
        // will be okay. This is used because localhost/developer machine is not
        // always synchronized with the server(s)
        int expectedMinTime = 2000 - maxTimeDiff;
        assertTrue("The total time should never be less the 'fileAge' ms (was " + time + ", expected "
                   + expectedMinTime + ")", time > expectedMinTime);

        assertEquals(TEST_MESSAGE, message.get());
    }

}
