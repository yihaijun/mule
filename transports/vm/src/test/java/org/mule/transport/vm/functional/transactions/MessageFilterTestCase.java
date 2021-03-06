/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.vm.functional.transactions;

import org.mule.api.DefaultMuleException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Test transaction behavior when "joinExternal" is set to disallow joining external transactions
 * There is one test per legal transactional behavior (e.g. ALWAYS_BEGIN).
 */
public class MessageFilterTestCase extends FunctionalTestCase
{

    protected static final Log logger = LogFactory.getLog(MessageFilterTestCase.class);

    private static String rejectMesage;

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/config/message-filter-config.xml";
    }

    /** Check that the configuration specifies considers external transactions */
    @Test
    public void testConfiguration() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        
        MuleMessage response = client.send("vm://order.validation", "OK", null);
        assertTrue(response.getPayload() instanceof NullPayload);
        assertEquals("OK(rejected!-1)", rejectMesage);
        
        response = client.send("vm://order.validation", "OK-ABC", null);
        assertTrue(response.getPayload() instanceof NullPayload);
        assertEquals("OK-ABC(rejected!-2)", rejectMesage);
        
        response = client.send("vm://order.validation", "OK-DEF", null);
        assertTrue(response.getPayload() instanceof NullPayload);
        assertEquals("OK-DEF(rejected!-1)", rejectMesage);
        rejectMesage = null;
        
        response = client.send("vm://order.validation", "OK-ABC-DEF", null);
        assertEquals("OK-ABC-DEF(success)", response.getPayloadAsString());
        assertNull(rejectMesage);
    }

    public static class Reject1 implements MessageProcessor
    {
        public void setName(String name)
        {
        }

        public MuleEvent process(MuleEvent event) throws MuleException
        {
            try
            {
                MuleMessage msg = event.getMessage();
                String payload = msg.getPayloadAsString();
                rejectMesage = payload + "(rejected!-1)";
                return null;
            }
            catch (Exception e)
            {
                throw new DefaultMuleException(e);
            }
        }
    }

     public static class Reject2 implements MessageProcessor
     {
         public void setName(String name)
         {
         }

        public MuleEvent process(MuleEvent event) throws MuleException
        {
            try
            {
                MuleMessage msg = event.getMessage();
                String payload = msg.getPayloadAsString();
                rejectMesage = payload + "(rejected!-2)";
                return null;
            }
            catch (Exception e)
            {
                throw new DefaultMuleException(e);
            }
        }
    }
}
