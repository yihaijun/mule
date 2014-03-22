/*
 * $Id: ExpressionFilter.java 20740 2010-12-15 10:29:17Z dirk.olmes $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters;

import static org.mule.util.ClassUtils.equal;
import static org.mule.util.ClassUtils.hash;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextAware;
import org.mule.api.routing.filter.Filter;
import org.mule.api.transport.PropertyScope;
import org.mule.expression.ExpressionConfig;
import org.mule.util.StringUtils;

import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.regaltec.asip.utils.PropertiesMapping;

/**
 * Allows boolean expressions to be executed on a message. Note that when using this filter you must be able to either specify
 * a boolean expression when using an expression filter or use one of the standard Mule filters.  These can be defined as follows -
 *
 * <ul>
 * <li>RegEx - 'regex:<pattern>': #[regex:'error' [0-9]]</li>
 * <li>Wildcard - 'wildcard:<pattern>': #[wildcard: *foo*</li>
 * <li>PayloadType - 'payload-type:<fully qualified class name>': #[payload:javax.jms.TextMessage]</li>
 * <li>ExceptionType - 'exception-type:<fully qualified class name>': #[exception-type:java.io.FileNotFoundException]</li>
 * <li>Header - 'header:<boolean expression>': #[header:foo!=null]</li>
 * </ul>
 *
 * Otherwise you can use eny expression filter providing you can define a boolean expression i.e.
 * <code>
 * #[xpath:count(/Foo/Bar) == 0]
 * </code>
 *
 * Note that it if the expression is not a boolean expression this filter will return true if the expression returns a result
 * 
 */
public class ExpressionFilter implements Filter, MuleContextAware
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(ExpressionFilter.class);

    private ExpressionConfig config;
    private String fullExpression;
    private boolean nullReturnsTrue = false;
    private MuleContext muleContext;

    /**
     * The class-loader that should be used to load any classes used in scripts.
     * Default to the classloader used to load this filter
     **/
    private ClassLoader expressionEvaluationClassLoader = Thread.currentThread().getContextClassLoader();
    
    /** For evaluators that are not expression languages we can delegate the execution to another filter */
    private Filter delegateFilter;

    public ExpressionFilter(String evaluator, String customEvaluator, String expression)
    {
        this.config = new ExpressionConfig(expression, evaluator, customEvaluator);
    }

    public ExpressionFilter(String evaluator, String expression)
    {
        this.config = new ExpressionConfig(expression, evaluator, null);
    }

    public ExpressionFilter(String expression)
    {
        this.config = new ExpressionConfig();
        this.config.parse(expression);
    }

    public ExpressionFilter()
    {
        super();
        this.config = new ExpressionConfig();
    }

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }

    /**
     * Check a given message against this filter.
     *
     * @param message a non null message to filter.
     * @return <code>true</code> if the message matches the filter
     */
    public boolean accept(MuleMessage message)
    {
        String expression = getExpression();
        Object payload = message.getPayload();
        if (expression.startsWith("org.apache.commons.lang.StringUtils.contains(payload[2].toString(),'")) {
            if (payload instanceof Object[]) {
                String dest =
                        expression.substring(expression.indexOf("].toString(),'") + "].toString(),'".length(),
                                expression.length() - 2);
                if (logger.isDebugEnabled()) {
                    logger.debug("expression=" + expression + ",dest=" + dest);
                }
                return org.apache.commons.lang.StringUtils.contains(((Object[]) payload)[2].toString(), dest);
            }
        }

        if (expression.startsWith("payload[") && expression.indexOf("equals('") > 0) {
            if (payload instanceof Object[] || payload instanceof String[]) {
                int index = Integer.parseInt(expression.substring("payload[".length(), expression.indexOf("]")));
                Object dest =
                        expression.substring(expression.indexOf("equals('") + "equals('".length(),
                                expression.length() - 2);
                if (((Object[]) payload)[index].equals(dest)) {
                    return true;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("expression=" + expression + ",payload[" + index + "]="
                                + ((Object[]) payload)[index]);
                    }
                    return false;
                }
            }
        }
        
        if (expression.equals("payload[0].length()>0") && payload instanceof Object[]) {
            if (((Object[]) payload)[0].toString().length() > 0) {
                return true;
            } else {
                return false;
            }
        }
        if (expression.startsWith("'${ida30.service.protocol}'.equals('")) {
            Properties kangarooProperties =
                    new PropertiesMapping("conf/custom-mule-start-expand-kangaroo.properties").getProperties();
            if (kangarooProperties == null || kangarooProperties.getProperty("ida30.service.protocol") == null) {
                return false;
            }
            if (kangarooProperties.getProperty("ida30.service.protocol").equalsIgnoreCase("vm")
                    && expression.startsWith("'${ida30.service.protocol}'.equals('vm')")) {
                return true;
            }
            if (kangarooProperties.getProperty("ida30.service.protocol").equalsIgnoreCase("jms")
                    && expression.startsWith("'${ida30.service.protocol}'.equals('jms')")) {
                return true;
            }
            return false;
        }

        if (expression.startsWith("payload instanceof com.")) {
            String instanceofClassName = expression.substring("payload instanceof ".length());
            if (payload.getClass().getName().equals(instanceofClassName)) {
                return true;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("expression=" + expression + ",payload.getClass().getName()="
                            + payload.getClass().getName());
                }
                return false;
            }
        }

        if (expression.startsWith("payload instanceof String")) {
            if (payload instanceof String) {
                return true;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("expression=" + expression + ",payload.getClass().getName()="
                            + payload.getClass().getName());
                }
                return false;
            }
        }

        if (expression.equals("payload.isXml()") && payload instanceof com.regaltec.asip.service.AsipServiceParamInf) {
            return ((com.regaltec.asip.service.AsipServiceParamInf) payload).isXml();
        }

        if (expression.startsWith("payload.getServiceName().equals(")
                && payload instanceof com.regaltec.asip.service.AsipServiceParamInf) {
            String equalsServiceName =
                    expression.substring("payload.getServiceName().equals('".length(), expression.length() - 2);
            String instanceofServiceName = ((com.regaltec.asip.service.AsipServiceParamInf) payload).getServiceName();
            if (instanceofServiceName.equals(equalsServiceName)) {
                return true;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("expression=" + expression + ",instanceofServiceName = " + instanceofServiceName);
                }
                return false;
            }
        }
        
        String expr = getFullExpression();
        if (delegateFilter != null)
        {
            boolean result = delegateFilter.accept(message);
            if (logger.isDebugEnabled())
            {
                logger.debug(MessageFormat.format("Result of expression filter: {0} is: {1}", expr, result));
            }
            return result;
        }

        // MULE-4797 Because there is no way to specify the class-loader that script
        // engines use and because scripts when used for expressions are compiled in
        // runtime rather than at initialization the only way to ensure the correct
        // class-loader to used is to switch it out here. We may want to consider
        // passing the class-loader to the ExpressionManager and only doing this for
        // certain ExpressionEvaluators further in.
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(expressionEvaluationClassLoader);
            return muleContext.getExpressionManager().evaluateBoolean(expr, message, nullReturnsTrue,
                !nullReturnsTrue);
        }
        finally
        {
            // Restore original context class-loader
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
    }

    protected String getFullExpression()
    {
        if(fullExpression==null)
        {
            //Handle non-expression filters
            if(config.getEvaluator().equals("header"))
            {
                delegateFilter = new MessagePropertyFilter(config.getExpression());
            }
            else if (config.getEvaluator().equals("variable"))
            {
                delegateFilter = new MessagePropertyFilter(PropertyScope.INVOCATION_NAME + ":"
                                                           + config.getExpression());
            }
            else if(config.getEvaluator().equals("regex"))
            {
                delegateFilter = new RegExFilter(config.getExpression());
            }
            else if(config.getEvaluator().equals("wildcard"))
            {
                delegateFilter = new WildcardFilter(config.getExpression());
            }
            else if(config.getEvaluator().equals("payload-type"))
            {
                try
                {
                    delegateFilter = new PayloadTypeFilter(config.getExpression());
                }
                catch (ClassNotFoundException e)
                {
                    IllegalArgumentException iae = new IllegalArgumentException();
                    iae.initCause(e);
                    throw iae;
                }
            }
            else if(config.getEvaluator().equals("exception-type"))
            {
                try
                {
                    if (StringUtils.isEmpty(config.getExpression()))
                    {
                        delegateFilter = new ExceptionTypeFilter();
                    }
                    else
                    {
                        delegateFilter = new ExceptionTypeFilter(config.getExpression());
                    }
                }
                catch (ClassNotFoundException e)
                {
                    IllegalArgumentException iae = new IllegalArgumentException();
                    iae.initCause(e);
                    throw iae;
                }
            }
            else
            {
                //In the case of 'payload' the expression can be null
                fullExpression = config.getFullExpression(muleContext.getExpressionManager());
            }
        }
        return fullExpression;
    }

    public String getCustomEvaluator()
    {
        return config.getCustomEvaluator();
    }

    public void setCustomEvaluator(String customEvaluator)
    {
        this.config.setCustomEvaluator(customEvaluator);
        fullExpression=null;        
    }

    public String getEvaluator()
    {
        return config.getEvaluator();
    }

    public void setEvaluator(String evaluator)
    {
        this.config.setEvaluator(evaluator);
        fullExpression=null;
    }

    public String getExpression()
    {
        return config.getExpression();
    }

    public void setExpression(String expression)
    {
        this.config.setExpression(expression);
        fullExpression=null;
    }

    public boolean isNullReturnsTrue()
    {
        return nullReturnsTrue;
    }

    public void setNullReturnsTrue(boolean nullReturnsTrue)
    {
        this.nullReturnsTrue = nullReturnsTrue;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final ExpressionFilter other = (ExpressionFilter) obj;
        return equal(config, other.config)
            && equal(delegateFilter, other.delegateFilter)
            && nullReturnsTrue == other.nullReturnsTrue;
    }

    @Override
    public int hashCode()
    {
        return hash(new Object[]{this.getClass(), config, delegateFilter, nullReturnsTrue});
    }
}
