/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.validation;

import org.junit.Test;

public class FalseValidationTestCase extends AbstractValidationModuleTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "validation-false-test-config.xml";
    }

    @Test
    public void validateTrue() throws Exception
    {
        this.validate("false", getTestEvent(""));
    }

}


