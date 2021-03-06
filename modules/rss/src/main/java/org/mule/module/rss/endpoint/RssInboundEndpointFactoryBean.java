/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.rss.endpoint;

import org.springframework.beans.factory.FactoryBean;

/**
 * TODO
 */
public class RssInboundEndpointFactoryBean extends RssEndpointBuilder implements FactoryBean
{
    public Class getObjectType()
    {
        return RssInboundEndpoint.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    public Object getObject() throws Exception
    {
        return buildInboundEndpoint();
    }

}
