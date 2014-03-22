/*
 * $Id: VMMessageDispatcher.java 22364 2011-07-11 02:58:20Z pablo.lagreca $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.vm;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transaction.TransactionCallback;
import org.mule.api.transport.DispatchException;
import org.mule.api.transport.NoReceiverForEndpointException;
import org.mule.config.i18n.CoreMessages;
import org.mule.transaction.TransactionTemplate;
import org.mule.transport.AbstractMessageDispatcher;
import org.mule.transport.vm.i18n.VMMessages;
import org.mule.util.queue.Queue;
import org.mule.util.queue.QueueSession;

import com.regaltec.asip.service.VMReceiverLimit;

/**
 * <code>VMMessageDispatcher</code> is used for providing in memory interaction
 * between components.
 */
public class VMMessageDispatcher extends AbstractMessageDispatcher
{
    private final VMConnector connector;

    public VMMessageDispatcher(OutboundEndpoint endpoint)
    {
        super(endpoint);
        this.connector = (VMConnector) endpoint.getConnector();
    }

    @Override
    protected void doDispatch(final MuleEvent event) throws Exception
    {
        EndpointURI endpointUri = endpoint.getEndpointURI();

        if (endpointUri == null)
        {
            throw new DispatchException(CoreMessages.objectIsNull("Endpoint"), event,
                (OutboundEndpoint) endpoint);
        }
        long beginTime = System.currentTimeMillis();
        long endTime = 0L;
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doDispatch(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        QueueSession session = connector.getQueueSession();
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doDispatch(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        Queue queue = session.getQueue(endpointUri.getAddress());
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doDispatch(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        if (!queue.offer(event, connector.getQueueTimeout()))
        {
            // queue is full
            throw new DispatchException(VMMessages.queueIsFull(queue.getName(), queue.size()),
                    event, (OutboundEndpoint) endpoint);
        }
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doDispatch(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("dispatched MuleEvent on endpointUri: " + endpointUri);
        }
    }

    @Override
    protected MuleMessage doSend(final MuleEvent event) throws Exception
    {
        long beginTime = System.currentTimeMillis();
        long endTime = 0L;
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doSend() expend " + (endTime - beginTime) + "ms!");
        }
        MuleMessage retMessage;
        EndpointURI endpointUri = event.getEndpoint().getEndpointURI();
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doSend(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        final VMMessageReceiver receiver = connector.getReceiver(endpointUri);
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doSend(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        // Apply any outbound transformers on this event before we dispatch
        
        if (receiver == null)
        {
            throw new NoReceiverForEndpointException(VMMessages.noReceiverForEndpoint(connector.getName(),
                                                                                      event.getEndpoint().getEndpointURI()));
        }

        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doSend(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }
        if(!VMReceiverLimit.gainReceiverPermit(receiver.getReceiverKey())){
            throw new NoReceiverForEndpointException(VMMessages.noReceiverForEndpoint(connector.getName(),
                    event.getEndpoint().getEndpointURI()));
        }
        endTime = System.currentTimeMillis();
        if(endTime - beginTime > 3L){
            logger.info("doSend(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
        }

        try {
            TransactionTemplate<MuleMessage> tt = new TransactionTemplate<MuleMessage>(
                                                                receiver.getEndpoint().getTransactionConfig(),
                                                                event.getMuleContext());
            endTime = System.currentTimeMillis();
            if(endTime - beginTime > 3L){
                logger.info("doSend(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
            }

            TransactionCallback<MuleMessage> cb = new TransactionCallback<MuleMessage>()
            {
                public MuleMessage doInTransaction() throws Exception
                {
                    return receiver.onCall(event.getMessage());
                }
            };
            endTime = System.currentTimeMillis();
            if(endTime - beginTime > 3L){
                logger.info("doSend(" + endpointUri.getAddress() + ") expend " + (endTime - beginTime) + "ms!");
            }
            retMessage = tt.execute(cb);
        } catch (Exception e) {
            throw e;
        }finally{
            VMReceiverLimit.returnReceiverPermit(receiver.getReceiverKey());
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("sent event on endpointUri: " + event.getEndpoint().getEndpointURI());
        }
        return retMessage;
    }

    @Override
    protected void doDispose()
    {
        // template method
    }

    @Override
    protected void doConnect() throws Exception
    {
        if (!endpoint.getExchangePattern().hasResponse())
        {
            // use the default queue profile to configure this queue.
            connector.getQueueProfile().configureQueue(
                    endpoint.getEndpointURI().getAddress(), connector.getQueueManager());
        }
    }

    @Override
    protected void doDisconnect() throws Exception
    {
        // template method
    }

}
