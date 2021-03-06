/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.config.ThreadingProfile;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.api.lifecycle.LifecycleState;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.ChainedThreadingProfile;
import org.mule.config.QueueProfile;
import org.mule.construct.SimpleFlowConstruct;
import org.mule.management.stats.QueueStatistics;
import org.mule.service.Pausable;
import org.mule.util.concurrent.Latch;

import java.beans.ExceptionListener;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class SedaStageInterceptingMessageProcessorTestCase extends
    OptionalAsyncInterceptingMessageProcessorTestCase implements ExceptionListener
{

    QueueProfile queueProfile = new QueueProfile();
    int queueTimeout;
    QueueStatistics queueStatistics;
    TestLifeCycleState lifeCycleState;

    @Override
    protected void doSetUp() throws Exception
    {
        queueStatistics = new TestQueueStatistics();
        queueTimeout = muleContext.getConfiguration().getDefaultQueueTimeout();
        lifeCycleState = new TestLifeCycleState();
        super.doSetUp();
        ((Initialisable) messageProcessor).initialise();
        ((Startable) messageProcessor).start();
        lifeCycleState.start();
    }

    @Override
    protected boolean isStartContext()
    {
        return false;
    }

    @Override
    protected void doTearDown() throws Exception
    {
        super.doTearDown();
        ((Stoppable) messageProcessor).stop();
        lifeCycleState.stop();
        lifeCycleState.dispose();

    }

    @Test
    public void testProcessOneWayThreadWaitTimeout() throws Exception
    {
        final int threadTimeout = 20;
        ThreadingProfile threadingProfile = new ChainedThreadingProfile(
            muleContext.getDefaultThreadingProfile());
        threadingProfile.setThreadWaitTimeout(threadTimeout);
        // Need 3 threads: 1 for polling, 2 to process work successfully without timeout
        threadingProfile.setMaxThreadsActive(3);
        threadingProfile.setPoolExhaustedAction(ThreadingProfile.WHEN_EXHAUSTED_WAIT);
        threadingProfile.setMuleContext(muleContext);

        MessageProcessor mockListener = mock(MessageProcessor.class);
        when(mockListener.process((MuleEvent) any())).thenAnswer(new Answer<MuleEvent>()
        {
            public MuleEvent answer(InvocationOnMock invocation) throws Throwable
            {
                Thread.sleep(threadTimeout * 2);
                return (MuleEvent) invocation.getArguments()[0];
            }
        });

        SedaStageInterceptingMessageProcessor sedaStageInterceptingMessageProcessor = new SedaStageInterceptingMessageProcessor(
            "testProcessOneWayThreadWaitTimeout", queueProfile, queueTimeout, threadingProfile,
            queueStatistics, muleContext);
        sedaStageInterceptingMessageProcessor.setListener(mockListener);
        sedaStageInterceptingMessageProcessor.initialise();
        sedaStageInterceptingMessageProcessor.start();

        MessagingExceptionHandler exceptionHandler = mock(MessagingExceptionHandler.class);
        SimpleFlowConstruct flow = mock(SimpleFlowConstruct.class);
        when(flow.getExceptionListener()).thenReturn(exceptionHandler);
        final MuleEvent event = getTestEvent(TEST_MESSAGE, flow, MessageExchangePattern.ONE_WAY);

        for (int i = 0; i < 3; i++)
        {
            sedaStageInterceptingMessageProcessor.process(event);
        }

        ArgumentMatcher<MuleEvent> notSameEvent = new ArgumentMatcher<MuleEvent>()
        {
            @Override
            public boolean matches(Object argument)
            {
                return !argument.equals(event);
            }
        };

        // Two events are processed
        verify(mockListener, timeout(RECEIVE_TIMEOUT).times(2)).process(argThat(notSameEvent));

        // One event gets processed by the exception strategy
        verify(exceptionHandler, timeout(RECEIVE_TIMEOUT).times(1)).handleException((Exception) any(),
            argThat(notSameEvent));

    }

    @Test
    public void testProcessOneWayWithException() throws Exception
    {
        final Latch latch = new Latch();
        ThreadingProfile threadingProfile = new ChainedThreadingProfile(
            muleContext.getDefaultThreadingProfile());
        threadingProfile.setMuleContext(muleContext);

        MessageProcessor mockListener = mock(MessageProcessor.class);
        when(mockListener.process((MuleEvent) any())).thenAnswer(new Answer<MuleEvent>()
        {
            public MuleEvent answer(InvocationOnMock invocation) throws Throwable
            {
                latch.countDown();
                throw new RuntimeException();
            }
        });

        SedaStageInterceptingMessageProcessor sedaStageInterceptingMessageProcessor = new SedaStageInterceptingMessageProcessor(
            "testProcessOneWayWithException", queueProfile, queueTimeout, threadingProfile, queueStatistics,
            muleContext);
        sedaStageInterceptingMessageProcessor.setListener(mockListener);
        sedaStageInterceptingMessageProcessor.initialise();
        sedaStageInterceptingMessageProcessor.start();

        MessagingExceptionHandler exceptionHandler = mock(MessagingExceptionHandler.class);
        SimpleFlowConstruct flow = mock(SimpleFlowConstruct.class);
        when(flow.getExceptionListener()).thenReturn(exceptionHandler);
        final MuleEvent event = getTestEvent(TEST_MESSAGE, flow, MessageExchangePattern.ONE_WAY);

        sedaStageInterceptingMessageProcessor.process(event);

        assertTrue(latch.await(RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));

        ArgumentMatcher<MuleEvent> notSameEvent = new ArgumentMatcher<MuleEvent>()
        {
            @Override
            public boolean matches(Object argument)
            {
                return !argument.equals(event);
            }
        };

        // One event get processed but then throws an exception
        verify(mockListener, timeout(RECEIVE_TIMEOUT).times(1)).process(argThat(notSameEvent));

        // One event gets processed by the exception strategy
        verify(exceptionHandler, timeout(RECEIVE_TIMEOUT).times(1)).handleException((Exception) any(),
            argThat(notSameEvent));

    }

    @Test(expected = RuntimeException.class)
    public void testProcessOneWayNoThreadingWithException() throws Exception
    {
        ThreadingProfile threadingProfile = new ChainedThreadingProfile(
            muleContext.getDefaultThreadingProfile());
        threadingProfile.setDoThreading(false);
        threadingProfile.setMuleContext(muleContext);

        MessageProcessor mockListener = mock(MessageProcessor.class);
        when(mockListener.process((MuleEvent) any())).thenThrow(new RuntimeException());

        SedaStageInterceptingMessageProcessor sedaStageInterceptingMessageProcessor = new SedaStageInterceptingMessageProcessor(
            "testProcessOneWayNoThreadingWithException", queueProfile, queueTimeout, threadingProfile,
            queueStatistics, muleContext);
        sedaStageInterceptingMessageProcessor.setListener(mockListener);
        sedaStageInterceptingMessageProcessor.initialise();
        sedaStageInterceptingMessageProcessor.start();

        MessagingExceptionHandler exceptionHandler = mock(MessagingExceptionHandler.class);
        SimpleFlowConstruct flow = mock(SimpleFlowConstruct.class);
        when(flow.getExceptionListener()).thenReturn(exceptionHandler);
        MuleEvent event = getTestEvent(TEST_MESSAGE, flow, MessageExchangePattern.ONE_WAY);

        sedaStageInterceptingMessageProcessor.process(event);
    }

    protected AsyncInterceptingMessageProcessor createAsyncInterceptingMessageProcessor(MessageProcessor listener)
        throws Exception
    {
        SedaStageInterceptingMessageProcessor mp = new SedaStageInterceptingMessageProcessor("name",
            queueProfile, queueTimeout, muleContext.getDefaultThreadingProfile(), queueStatistics,
            muleContext);
        mp.setListener(listener);
        return mp;
    }

    @Test
    public void testSpiWorkThrowableHandling() throws Exception
    {
        try
        {
            new AsyncWorkListener(getSensingNullMessageProcessor()).handleWorkException(getTestWorkEvent(),
                "workRejected");
        }
        catch (MuleRuntimeException mrex)
        {
            assertNotNull(mrex);
            assertTrue(mrex.getCause().getClass() == Throwable.class);
            assertEquals("testThrowable", mrex.getCause().getMessage());
        }
    }

    private WorkEvent getTestWorkEvent()
    {
        return new WorkEvent(this, // source
            WorkEvent.WORK_REJECTED, getTestWork(), new WorkException(new Throwable("testThrowable")));
    }

    private Work getTestWork()
    {
        return new Work()
        {
            public void release()
            {
                // noop
            }

            public void run()
            {
                // noop
            }
        };
    }

    class TestQueueStatistics implements QueueStatistics
    {
        int incCount;
        int decCount;

        public void decQueuedEvent()
        {
            decCount++;
        }

        public void incQueuedEvent()
        {
            incCount++;
        }

        public boolean isEnabled()
        {
            return true;
        }
    }

    class TestLifeCycleState implements LifecycleState, Lifecycle
    {

        AtomicBoolean started = new AtomicBoolean(false);
        AtomicBoolean stopped = new AtomicBoolean(true);
        AtomicBoolean disposed = new AtomicBoolean(false);
        AtomicBoolean initialised = new AtomicBoolean(false);
        AtomicBoolean paused = new AtomicBoolean(false);

        public boolean isDisposed()
        {
            return disposed.get();
        }

        public boolean isDisposing()
        {
            return false;
        }

        public boolean isInitialised()
        {
            return initialised.get();
        }

        public boolean isInitialising()
        {
            return false;
        }

        public boolean isPhaseComplete(String phase)
        {
            if (Pausable.PHASE_NAME.equals(phase))
            {
                return paused.get();
            }
            else
            {
                return false;
            }
        }

        public boolean isPhaseExecuting(String phase)
        {
            return false;
        }

        public boolean isStarted()
        {
            return started.get();
        }

        public boolean isStarting()
        {
            return false;
        }

        public boolean isStopped()
        {
            return stopped.get();
        }

        public boolean isStopping()
        {
            return false;
        }

        public void initialise() throws InitialisationException
        {
            initialised.set(true);
        }

        public void start() throws MuleException
        {
            initialised.set(false);
            stopped.set(false);
            started.set(true);
        }

        public void stop() throws MuleException
        {
            started.set(false);
            stopped.set(true);
        }

        public void dispose()
        {
            stopped.set(true);
            disposed.set(true);
        }

        public boolean isValidTransition(String phase)
        {
            return false;
        }
    }

}
