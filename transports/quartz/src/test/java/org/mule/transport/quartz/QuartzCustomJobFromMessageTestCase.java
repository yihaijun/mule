/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.quartz;

import org.mule.api.client.MuleClient;
import org.mule.tck.functional.CountdownCallback;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;
import org.mule.transport.quartz.jobs.ScheduledDispatchJobConfig;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuartzCustomJobFromMessageTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "quartz-receive-dispatch-delegating-job.xml";
    }

    @Test
    public void testDelegatingJobAsProperty() throws Exception
    {
        FunctionalTestComponent component = (FunctionalTestComponent) getComponent("scheduledService");
        assertNotNull(component);
        CountdownCallback count = new CountdownCallback(1);
        component.setEventCallback(count);

        Map<String, Object> props = new HashMap<String, Object>();
        ScheduledDispatchJobConfig jobConfig = new ScheduledDispatchJobConfig();
        jobConfig.setMuleContext(muleContext);
        jobConfig.setEndpointRef("vm://quartz.in");
        props.put(QuartzConnector.PROPERTY_JOB_CONFIG, jobConfig);

        MuleClient client = muleContext.getClient();
        client.dispatch("vm://quartz.scheduler1", NullPayload.getInstance(), props);
        assertTrue(count.await(7000));

        // now that the scheduler sent the event, null out the event callback to avoid CountdownCallback
        // report more messages than requested during shutdown of the test/Mule server
        component.setEventCallback(null);
    }

    @Test
    public void testDelegatingJobAsPayload() throws Exception
    {
        FunctionalTestComponent component = (FunctionalTestComponent) getComponent("scheduledService");
        assertNotNull(component);
        CountdownCallback count = new CountdownCallback(1);
        component.setEventCallback(count);

        ScheduledDispatchJobConfig jobConfig = new ScheduledDispatchJobConfig();
        jobConfig.setMuleContext(muleContext);
        jobConfig.setEndpointRef("vm://quartz.in");

        MuleClient client = muleContext.getClient();
        client.dispatch("vm://quartz.scheduler2", jobConfig, null);
        assertTrue(count.await(7000));

        // now that the scheduler sent the event, null out the event callback to avoid CountdownCallback
        // report more messages than requested during shutdown of the test/Mule server
        component.setEventCallback(null);
    }
}
