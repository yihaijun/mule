/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.validation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.mule.api.MessagingException;
import org.mule.module.validation.exception.ValidationResultException;
import org.mule.tck.junit4.FunctionalTestCase;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

public class AllValidationTestCase extends FunctionalTestCase
{

    private static final int FAILING_VALIDATIONS = 2;

    @Override
    protected String getConfigResources()
    {
        return "validation-all-test-config.xml";
    }

    @Test
    public void failures() throws Exception
    {
        try
        {
            this.testFlow("failures", getTestEvent(""));
            fail("Was expecting a failure");
        }
        catch (MessagingException e)
        {
            this.validateException(e);
        }
    }

    @Test
    public void failuresWithoutException() throws Exception
    {
        ValidationResult result = (ValidationResult) this.runFlow("failuresWithoutException")
            .getMessage()
            .getPayload();

        this.validateFailingResult(result);

    }

    @Test
    public void noFailures() throws Exception
    {
        ValidationResult result = (ValidationResult) this.runFlow("noFailures").getMessage().getPayload();

        assertTrue(CollectionUtils.isEmpty(result.getMessages()));
        assertFalse(result.hasErros());
    }

    private void validateException(MessagingException e)
    {
        Exception cause = (Exception) e.getCause();
        assertTrue(cause instanceof ValidationResultException);
        ValidationResultException result = (ValidationResultException) cause;
        this.validateFailingResult(result.getValidationResult());
    }

    private void validateFailingResult(ValidationResult result)
    {
        assertTrue(result.hasErros());
        assertEquals(FAILING_VALIDATIONS, result.getMessages().size());

    }

}
