/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.xml.expression;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.javabean.JavaBeanXPath;

/** TODO */
public class BeanPayloadExpressionEvaluator extends AbstractXPathExpressionEvaluator
{
    public static final String NAME = "bean";

    protected XPath createXPath(String expression, Object object) throws JaxenException
    {
        expression = expression.replaceAll("[.]", "/");
        return new JavaBeanXPath(expression);
    }

    protected Object extractResultFromNode(Object result)
    {
        if(result instanceof org.jaxen.javabean.Element)
        {
            return ((org.jaxen.javabean.Element)result).getObject();
        }
        return result;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return NAME;
    }
}
