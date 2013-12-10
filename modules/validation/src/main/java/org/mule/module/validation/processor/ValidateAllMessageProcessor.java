/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.devkit.processor.ExpressionEvaluatorSupport;
import org.mule.module.validation.ValidationResult;
import org.mule.module.validation.exception.ValidationResultException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Executes all configured validations and throws one single exception with the
 * messages for all the failed ones. Unlike the other messageProcessors, the
 * exception thrown by this one is a {@link ValidationResultException} and cannot be
 * customized. However, you can configure if you either want this validator to throw
 * an exception (default) or set the message payload to an instance of
 * {@link ValidationResult} with all the messages
 */
public class ValidateAllMessageProcessor extends ExpressionEvaluatorSupport
    implements MessageProcessor, Initialisable
{

    /**
     * A boolean expression that if evaluated to <code>true</code> will throw a
     * {@link ValidationResultException} collecting the messages of all failed
     * validations. Otherwise, message payload will be set to a
     * {@link ValidationResultException}
     */
    private String throwsException = "true";

    /**
     * Validators to be executed. {@link IllegalStateException} will be thrown if
     * this collection is <code>null</code> or has less than one element
     */
    private List<AbstractValidationMessageProcessor> messageProcessors;

    @Override
    public void initialise() throws InitialisationException
    {
        if (CollectionUtils.isEmpty(this.messageProcessors))
        {
            throw new IllegalStateException("Validate all must have at least one nested validator");
        }
    }

    @Override
    public MuleEvent process(MuleEvent event) throws MuleException
    {
        final boolean throwsException = (Boolean) this.evaluateAndTransform(event.getMuleContext(), event,
            Boolean.class, null, this.throwsException);
        
        List<String> messages = new ArrayList<String>(this.messageProcessors.size());
        for (AbstractValidationMessageProcessor validator : this.messageProcessors)
        {
            try
            {
                validator.process(event);
            }
            catch (Exception e)
            {
                messages.add(e.getMessage());
            }
        }

        ValidationResult result = new ValidationResult(event, messages);

        if (throwsException)
        {
            throw new ValidationResultException(result);
        }
        else
        {
            event.getMessage().setPayload(result);
        }

        return event;
    }

    public void setThrowsException(String throwsException)
    {
        this.throwsException = throwsException;
    }

    public void setMessageProcessors(List<AbstractValidationMessageProcessor> validators)
    {
        this.messageProcessors = validators;
    }

}
