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
import static org.mockito.Mockito.mock;

import org.mule.api.MuleEvent;
import org.mule.api.store.ObjectStore;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.tck.size.SmallTest;

import java.util.Arrays;

import org.junit.Test;

@SmallTest
public class ValidationResultTestCase extends AbstractMuleContextTestCase
{

    private static final String MESSAGE = "kaboom!";

    @Test
    public void hasNoErrors()
    {
        assertFalse(this.buildResult().hasErros());
        assertFalse(this.buildResult(new String[]{}).hasErros());
    }

    @Test
    public void hasErrros()
    {
        assertTrue(this.buildResult(MESSAGE).hasErros());
    }

    @Test
    public void testToString()
    {
        ValidationResult result = buildResult(MESSAGE, MESSAGE);
        assertEquals(String.format("%s\n%s", MESSAGE, MESSAGE), result.toString());
    }

    @Test
    public void serialization() throws Exception
    {
        ObjectStore<ValidationResult> os = null;
        final String key = "key";
        try
        {
            os = muleContext.getObjectStoreManager().getObjectStore("serializationTest", true);
            os.store(key, this.buildResult(MESSAGE));

            assertTrue(os.contains(key));
            ValidationResult result = os.retrieve(key);
            assertTrue(result.hasErros());
            assertEquals(1, result.getMessages().size());
            assertEquals(MESSAGE, result.getMessages().get(0));
        }
        finally
        {
            if (os != null)
            {
                muleContext.getObjectStoreManager().disposeStore(os);
            }
        }
    }

    private ValidationResult buildResult(String... messages)
    {
        if (messages != null)
        {
            return new ValidationResult(mock(MuleEvent.class), Arrays.asList(messages));
        }
        else
        {
            return new ValidationResult(mock(MuleEvent.class), null);
        }

    }
}
