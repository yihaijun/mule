/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.sxc;

public class SxcFilterWithGlobalNamespaceTestCase extends SxcFilterTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "xpath-filter-with-global-naespace-conf.xml";
    }
}
