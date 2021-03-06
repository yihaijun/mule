/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.spring.parsers;

import org.junit.Test;

public class EndpointServiceConflictTestCase extends AbstractBadConfigTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/config/spring/parsers/endpoint-service-conflict-test.xml";
    }

    @Test
    public void testBeanError() throws Exception
    {
        assertErrorContains("A service named LenderService already exists");
    }

}
