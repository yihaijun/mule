/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation.processor;

import org.mule.api.DefaultMuleException;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.i18n.MessageFactory;
import org.mule.devkit.processor.ExpressionEvaluatorSupport;
import org.mule.module.validation.Locale;
import org.mule.module.validation.Validator;
import org.mule.module.validation.exception.ValidationException;
import org.mule.util.ClassUtils;

public abstract class AbstractValidationMessageProcessor extends ExpressionEvaluatorSupport
    implements MessageProcessor, MuleContextAware
{

    protected static final Locale DEFAULT_LOCALE = Locale.US;

    private String exceptionClass;
    private String message = null;
    protected MuleContext muleContext;

    @Override
    public final MuleEvent process(MuleEvent event) throws MuleException
    {
        Validator validator = null;
        try
        {
            validator = this.getValidator(event);
        }
        catch (Exception e)
        {
            throw new DefaultMuleException(
                MessageFactory.createStaticMessage("Exception found while creating validator"), e);
        }

        if (!validator.isValid(event))
        {
            String message;
            if (this.message != null)
            {
                try
                {
                    message = (String) this.evaluateAndTransform(this.muleContext, event, String.class, null,
                        this.message);
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException("Could not evaluate custom exception message", e);
                }
            }
            else
            {
                message = validator.getErrorMessage(event);
            }

            throw new MessagingException(event, this.buildException(message, event));
        }

        return event;
    }

    private Exception buildException(String message, MuleEvent event)
    {
        try
        {
            return ClassUtils.instanciateClass(this.getExceptionClass(event), new Object[]{message});
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            return new DefaultMuleException(
                MessageFactory.createStaticMessage("Failed to create validation exception"), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Exception> getExceptionClass(MuleEvent event)
    {
        if (this.exceptionClass == null)
        {
            return ValidationException.class;
        }

        Class<? extends Exception> exceptionClass = null;
        try
        {
            exceptionClass = (Class<? extends Exception>) this.evaluateAndTransform(this.muleContext, event,
                Class.class, null, this.exceptionClass);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(String.format(
                "Custom exception class %s was not found in classpath", this.exceptionClass));
        }

        if (!Exception.class.isAssignableFrom(exceptionClass))
        {
            throw new IllegalArgumentException(String.format(
                "Was expecting an exception type, %s found instead", exceptionClass.getCanonicalName()));
        }

        if (ClassUtils.getConstructor(exceptionClass, new Class[]{String.class}) == null)
        {
            throw new IllegalArgumentException(
                String.format(
                    "Exception class must contain a constructor with a single String argument. %s doesn't have a matching constructor",
                    exceptionClass.getCanonicalName()));
        }

        return exceptionClass;

    }

    protected abstract Validator getValidator(MuleEvent event) throws Exception;

    public final void setExceptionClass(String exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }

    public final void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public final void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }
}
