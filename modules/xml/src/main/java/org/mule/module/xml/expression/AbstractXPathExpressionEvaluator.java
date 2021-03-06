/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.xml.expression;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.context.notification.MuleContextNotificationListener;
import org.mule.api.expression.ExpressionEvaluator;
import org.mule.api.expression.ExpressionRuntimeException;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.RegistrationException;
import org.mule.config.i18n.CoreMessages;
import org.mule.context.notification.MuleContextNotification;
import org.mule.module.xml.i18n.XmlMessages;
import org.mule.module.xml.util.NamespaceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.dom4j.Document;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.mule.transformer.types.DataTypeFactory;

/**
 * Provides a base class for XPath property extractors. The XPath engine used is jaxen (http://jaxen.org) which supports
 * XPath queries on other object models such as JavaBeans as well as Xml
 */
public abstract class AbstractXPathExpressionEvaluator implements ExpressionEvaluator, Initialisable, Disposable, MuleContextAware
{
    private Map<String, XPath> cache = new WeakHashMap<String, XPath>(8);

    private MuleContext muleContext;
    private NamespaceManager namespaceManager;

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;

    }

    public void initialise() throws InitialisationException
    {
        try
        {
            /*
                Workaround for standalone mode, when registry bootstrap order may be non-deterministic and lead
                to failures on startup.

                initialise() can't do any lookups as it will have spring create and init beans for things like
                global endpoints, interfering with current lifecycle and leading to failure.
                TODO AP/RM this will be solved by the @Inject annotation or phase, as discussed
             */
            this.muleContext.registerListener(new MuleContextNotificationListener<MuleContextNotification>(){

                public void onNotification(MuleContextNotification notification)
                {
                    // CONTEXT_INITIALIZED fires too soon, before registry is inited, thus using this one
                    if (MuleContextNotification.CONTEXT_STARTING == notification.getAction())
                    {
                        try
                        {
                            namespaceManager = muleContext.getRegistry().lookupObject(NamespaceManager.class);
                        }
                        catch (RegistrationException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
        catch (Throwable t)
        {
            throw new InitialisationException(t, this);
        }
    }

    public void inject() throws Exception
    {
        try
        {
            namespaceManager = muleContext.getRegistry().lookupObject(NamespaceManager.class);
        }
        catch (RegistrationException e)
        {
            throw new ExpressionRuntimeException(CoreMessages.failedToLoad("NamespaceManager"), e);
        }
    }

    /** {@inheritDoc} */
    public Object evaluate(String expression, MuleMessage message)
    {
        try
        {
            Object payload = message.getPayload();
            //we need to convert to a Dom if its an XML string
            if(payload instanceof String)
            {
                payload = message.getPayload(DataTypeFactory.create(Document.class));
            }

            List<?> result;

            /*  XPath context state is not thread safe so synchronization must be enforced when adding a new namespace and
                on evaluation when the context is read.
             */
            XPath xpath = getXPath(expression, payload);
            synchronized (xpath)
            {
                result = xpath.selectNodes(payload);
            }

            result = extractResultsFromNodes(result);
            if(result.size()==1)
            {
                return result.get(0);
            }
            else if(result.size()==0)
            {
                return null;
            }
            else
            {
                return result;
            }
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(XmlMessages.failedToProcessXPath(expression), e);
        }
    }

    protected void addNamespaces(NamespaceManager manager, XPath xpath)
    {
        for (Iterator<?> iterator = manager.getNamespaces().entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)iterator.next();
            try
            {
                xpath.addNamespace(entry.getKey().toString(), entry.getValue().toString());
            }
            catch (JaxenException e)
            {
                throw new ExpressionRuntimeException(XmlMessages.failedToRegisterNamespace(entry.getKey().toString(), entry.getValue().toString()));
            }
        }
    }

    /** {@inheritDoc} */
    public final void setName(String name)
    {
        throw new UnsupportedOperationException("setName");
    }

    /*
        The cache is not thread safe, more than one instance of the same xpath can be created, it wouldn't be an
        issue in this case since one will eventually be selected for GC
        A ReadWriteLock could be used to guard the cache but it would add unnecessary complexity
     */
    protected XPath getXPath(String expression, Object object) throws JaxenException
    {
        String xPathCacheKey = expression + getXPathClassName(object);
        XPath xpath = cache.get(xPathCacheKey);
        if(xpath==null)
        {
            xpath = createXPath(expression, object);
            synchronized (xpath)
            {
                if(namespaceManager!=null)
                {
                    addNamespaces(namespaceManager, xpath);
                }
            }
            cache.put(xPathCacheKey, xpath);
        }
        return xpath;
    }

    protected String getXPathClassName(Object object)
    {
        return getClass().getName();
    }

    protected abstract XPath createXPath(String expression, Object object) throws JaxenException;

    protected List<?> extractResultsFromNodes(List<?> results)
    {
        if (results == null)
        {
            return null;
        }
        List<Object> newResults = new ArrayList<Object>(results.size());
        for (Object o : results)
        {
            newResults.add(extractResultFromNode(o));
        }
        return newResults;
    }

    /**
     * A lifecycle method where implementor should free up any resources. If an
     * exception is thrown it should just be logged and processing should continue.
     * This method should not throw Runtime exceptions.
     */
    public void dispose()
    {
        cache.clear();
    }

    public NamespaceManager getNamespaceManager()
    {
        return namespaceManager;
    }

    public void setNamespaceManager(NamespaceManager namespaceManager)
    {
        this.namespaceManager = namespaceManager;
    }

    public MuleContext getMuleContext()
    {
        return muleContext;
    }

    protected abstract Object extractResultFromNode(Object result);
}
