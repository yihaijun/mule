/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.mule.DefaultMessageCollection;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.MuleMessageCollection;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.service.Service;
import org.mule.tck.MuleTestUtils;
import org.mule.tck.SensingNullMessageProcessor;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.tck.testmodels.fruit.Apple;

import java.util.List;

import org.junit.Test;

public class SimpleCollectionAggregatorTestCase extends AbstractMuleContextTestCase
{

    public SimpleCollectionAggregatorTestCase()
    {
        setStartContext(true);
    }

    @Test
    public void testAggregateMultipleEvents() throws Exception
    {
        MuleSession session = getTestSession(getTestService(), muleContext);
        Service testService = getTestService("test", Apple.class);
        assertNotNull(testService);

        SimpleCollectionAggregator router = new SimpleCollectionAggregator();
        SensingNullMessageProcessor sensingMessageProcessor = getSensingNullMessageProcessor();
        router.setListener(sensingMessageProcessor);
        router.setMuleContext(muleContext);
        router.setFlowConstruct(testService);
        router.initialise();

        MuleMessage message1 = new DefaultMuleMessage("test event A", muleContext);
        MuleMessage message2 = new DefaultMuleMessage("test event B", muleContext);
        MuleMessage message3 = new DefaultMuleMessage("test event C", muleContext);
        message1.setCorrelationId(message1.getUniqueId());
        message2.setCorrelationId(message1.getUniqueId());
        message3.setCorrelationId(message1.getUniqueId());
        message1.setCorrelationGroupSize(3);

        ImmutableEndpoint endpoint = MuleTestUtils.getTestOutboundEndpoint(MessageExchangePattern.ONE_WAY,
            muleContext);
        MuleEvent event1 = new DefaultMuleEvent(message1, endpoint, session);
        MuleEvent event2 = new DefaultMuleEvent(message2, endpoint, session);
        MuleEvent event3 = new DefaultMuleEvent(message3, endpoint, session);

        assertNull(router.process(event1));
        assertNull(router.process(event2));
        assertNull(router.process(event3));

        assertNotNull(sensingMessageProcessor.event);
        MuleMessage nextMessage = sensingMessageProcessor.event.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage instanceof MuleMessageCollection);
        assertTrue(nextMessage.getPayload() instanceof List<?>);
        List<String> payload = (List<String>) nextMessage.getPayload();
        assertEquals(3, payload.size());
        assertEquals("test event A", payload.get(0));
        assertEquals("test event B", payload.get(1));
        assertEquals("test event C", payload.get(2));
    }

    @Test
    public void testAggregateSingleEvent() throws Exception
    {
        MuleSession session = getTestSession(getTestService(), muleContext);
        Service testService = getTestService("test", Apple.class);
        assertNotNull(testService);

        SimpleCollectionAggregator router = new SimpleCollectionAggregator();
        SensingNullMessageProcessor sensingMessageProcessor = getSensingNullMessageProcessor();
        router.setListener(sensingMessageProcessor);
        router.setMuleContext(muleContext);
        router.setFlowConstruct(testService);
        router.initialise();

        MuleMessage message1 = new DefaultMuleMessage("test event A", muleContext);
        message1.setCorrelationId(message1.getUniqueId());
        message1.setCorrelationGroupSize(1);

        ImmutableEndpoint endpoint = MuleTestUtils.getTestOutboundEndpoint(MessageExchangePattern.ONE_WAY,
            muleContext);
        MuleEvent event1 = new DefaultMuleEvent(message1, endpoint, session);

        MuleEvent resultEvent = router.process(event1);
        assertNull(resultEvent);

        assertNotNull(sensingMessageProcessor.event);
        MuleMessage nextMessage = sensingMessageProcessor.event.getMessage();
        assertNotNull(nextMessage);
        assertTrue(nextMessage instanceof MuleMessageCollection);
        assertTrue(nextMessage.getPayload() instanceof List<?>);
        List<String> payload = (List<String>) nextMessage.getPayload();
        assertEquals(1, payload.size());
        assertEquals("test event A", payload.get(0));
    }

    @Test
    public void testAggregateMessageCollections() throws Exception
    {
        MuleSession session = getTestSession(getTestService(), muleContext);
        Service testService = getTestService("test", Apple.class);
        assertNotNull(testService);

        SimpleCollectionAggregator router = new SimpleCollectionAggregator();
        router.setMuleContext(muleContext);
        router.setFlowConstruct(testService);
        router.initialise();

        MuleMessage message1 = new DefaultMuleMessage("test event A", muleContext);
        MuleMessage message2 = new DefaultMuleMessage("test event B", muleContext);
        MuleMessage message3 = new DefaultMuleMessage("test event C", muleContext);
        MuleMessage message4 = new DefaultMuleMessage("test event D", muleContext);
        MuleMessageCollection messageCollection1 = new DefaultMessageCollection(muleContext);
        MuleMessageCollection messageCollection2 = new DefaultMessageCollection(muleContext);
        messageCollection1.addMessage(message1);
        messageCollection1.addMessage(message2);
        messageCollection2.addMessage(message3);
        messageCollection2.addMessage(message4);

        messageCollection1.setCorrelationGroupSize(2);
        messageCollection1.setCorrelationId(messageCollection1.getUniqueId());
        messageCollection2.setCorrelationGroupSize(2);
        messageCollection2.setCorrelationId(messageCollection1.getUniqueId());

        ImmutableEndpoint endpoint = MuleTestUtils.getTestOutboundEndpoint(MessageExchangePattern.ONE_WAY,
            muleContext);
        MuleEvent event1 = new DefaultMuleEvent(messageCollection1, endpoint, session);
        MuleEvent event2 = new DefaultMuleEvent(messageCollection2, endpoint, session);

        assertNull(router.process(event1));
        MuleEvent resultEvent = router.process(event2);
        assertNotNull(resultEvent);
        MuleMessage resultMessage = resultEvent.getMessage();
        assertNotNull(resultMessage);
        List<List<String>> payload = (List<List<String>>) resultMessage.getPayload();
        assertEquals(2, payload.size());
        assertEquals("test event A", ((List) payload.get(0)).get(0));
        assertEquals("test event B", ((List) payload.get(0)).get(1));
        assertEquals("test event C", ((List) payload.get(1)).get(0));
        assertEquals("test event D", ((List) payload.get(1)).get(1));

    }

}
