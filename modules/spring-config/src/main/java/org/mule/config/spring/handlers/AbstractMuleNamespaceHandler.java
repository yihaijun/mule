/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.spring.handlers;

import org.mule.config.spring.factories.InboundEndpointFactoryBean;
import org.mule.config.spring.factories.OutboundEndpointFactoryBean;
import org.mule.config.spring.parsers.MuleDefinitionParser;
import org.mule.config.spring.parsers.MuleDefinitionParserConfiguration;
import org.mule.config.spring.parsers.PostProcessor;
import org.mule.config.spring.parsers.PreProcessor;
import org.mule.config.spring.parsers.assembly.configuration.ValueMap;
import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportGlobalEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.support.AddressedEndpointDefinitionParser;
import org.mule.endpoint.EndpointURIEndpointBuilder;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.IOUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * This Namespace handler extends the default Spring {@link org.springframework.beans.factory.xml.NamespaceHandlerSupport}
 * to allow certain elements in document to be ignored by the handler.
 */
public abstract class AbstractMuleNamespaceHandler extends NamespaceHandlerSupport
{
    public static final String GLOBAL_ENDPOINT = "endpoint";
    public static final String INBOUND_ENDPOINT = "inbound-endpoint";
    public static final String OUTBOUND_ENDPOINT = "outbound-endpoint"; 

    protected transient final Log logger = LogFactory.getLog(getClass());

    /**
     * @param name The name of the element to be ignored.
     */
    protected final void registerIgnoredElement(String name)
    {
        registerBeanDefinitionParser(name, new IgnoredDefinitionParser());
    }

    protected MuleDefinitionParserConfiguration registerConnectorDefinitionParser(Class connectorClass, String transportName)
    {
        return registerConnectorDefinitionParser(findConnectorClass(connectorClass, transportName));
    }

    protected MuleDefinitionParserConfiguration registerConnectorDefinitionParser(Class connectorClass)
    {
        return registerConnectorDefinitionParser( new MuleOrphanDefinitionParser(connectorClass, true));
    }

    protected MuleDefinitionParserConfiguration registerConnectorDefinitionParser(MuleDefinitionParser parser)
    {
        registerBeanDefinitionParser("connector", parser);
        return parser;
    }

    protected MuleDefinitionParserConfiguration registerMuleBeanDefinitionParser(String name, MuleDefinitionParser parser)
    {
        registerBeanDefinitionParser(name, parser);
        return parser;
    }

    protected MuleDefinitionParserConfiguration registerStandardTransportEndpoints(String protocol, String[] requiredAttributes)
    {
        return new RegisteredMdps(protocol, AddressedEndpointDefinitionParser.PROTOCOL, requiredAttributes);
    }

    protected MuleDefinitionParserConfiguration registerMetaTransportEndpoints(String protocol)
    {
        return new RegisteredMdps(protocol, AddressedEndpointDefinitionParser.META, new String[]{});
    }

    private static class IgnoredDefinitionParser implements BeanDefinitionParser
    {
        public IgnoredDefinitionParser()
        {
            super();
        }
        
        public BeanDefinition parse(Element element, ParserContext parserContext)
        {
            return null;
        }
    }

    protected Class getInboundEndpointFactoryBeanClass()
    {
        return InboundEndpointFactoryBean.class;
    }

    protected Class getOutboundEndpointFactoryBeanClass()
    {
        return OutboundEndpointFactoryBean.class;
    }

    protected Class getGlobalEndpointBuilderBeanClass()
    {
        return EndpointURIEndpointBuilder.class;
    }

    private class RegisteredMdps implements MuleDefinitionParserConfiguration
    {
        private Set bdps = new HashSet();

        public RegisteredMdps(String protocol, boolean isMeta, String[] requiredAttributes)
        {
            registerBeanDefinitionParser("endpoint", add(new TransportGlobalEndpointDefinitionParser(protocol, isMeta, AbstractMuleNamespaceHandler.this.getGlobalEndpointBuilderBeanClass(), requiredAttributes, new String[]{})));
            registerBeanDefinitionParser("inbound-endpoint", add(new TransportEndpointDefinitionParser(protocol, isMeta, AbstractMuleNamespaceHandler.this.getInboundEndpointFactoryBeanClass(), requiredAttributes, new String[]{})));
            registerBeanDefinitionParser("outbound-endpoint", add(new TransportEndpointDefinitionParser(protocol, isMeta, AbstractMuleNamespaceHandler.this.getOutboundEndpointFactoryBeanClass(), requiredAttributes, new String[]{})));
        }

        private MuleDefinitionParser add(MuleDefinitionParser bdp)
        {
            bdps.add(bdp);
            return bdp;
        }

        public MuleDefinitionParserConfiguration registerPreProcessor(PreProcessor preProcessor)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).registerPreProcessor(preProcessor);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration registerPostProcessor(PostProcessor postProcessor)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).registerPostProcessor(postProcessor);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addReference(String propertyName)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addReference(propertyName);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addMapping(String propertyName, Map mappings)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addMapping(propertyName, mappings);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addMapping(String propertyName, String mappings)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addMapping(propertyName, mappings);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addMapping(String propertyName, ValueMap mappings)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addMapping(propertyName, mappings);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addAlias(String alias, String propertyName)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addAlias(alias, propertyName);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addCollection(String propertyName)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addCollection(propertyName);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addIgnored(String propertyName)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addIgnored(propertyName);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration removeIgnored(String propertyName)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).removeIgnored(propertyName);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration setIgnoredDefault(boolean ignoreAll)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).setIgnoredDefault(ignoreAll);
            }
            return this;
        }

        public MuleDefinitionParserConfiguration addBeanFlag(String flag)
        {
            for (Iterator bdp = bdps.iterator(); bdp.hasNext();)
            {
                ((MuleDefinitionParserConfiguration) bdp.next()).addBeanFlag(flag);
            }
            return this;
        }
    }
    
    /**
     * Subclasses can call this to register the supplied {@link BeanDefinitionParser} to
     * handle the specified element. The element name is the local (non-namespace qualified)
     * name.
     */
    protected void registerDeprecatedBeanDefinitionParser(String elementName, BeanDefinitionParser parser, String deprecationWarning) 
    {
        if (parser instanceof MuleDefinitionParserConfiguration)
        {
            ((MuleDefinitionParser) parser).setDeprecationWarning(deprecationWarning);
        }
        registerBeanDefinitionParser(elementName, parser);
    }

    /**
     * See if there's a preferred connector class
     */
    protected Class findConnectorClass(Class basicConnector, String transportName)
    {
        String preferredPropertiesURL = "META-INF/services/org/mule/transport/preferred-" +transportName + ".properties";
        InputStream stream = AbstractMuleNamespaceHandler.class.getClassLoader().getResourceAsStream(preferredPropertiesURL);
        if (stream != null)
        {
            try
            {
                Properties preferredProperties = new Properties();
                preferredProperties.load(stream);
                String preferredConnectorName = preferredProperties.getProperty("connector");
                if (preferredConnectorName != null)
                {
                    logger.debug("Found preferred connector class " + preferredConnectorName);
                    return Class.forName(preferredConnectorName);
                }
            }
            catch (Exception e)
            {
                logger.debug("Error processing preferred properties", e);
            }
            finally
            {
                IOUtils.closeQuietly(stream);
            }
        }
        return basicConnector;
    }
}
