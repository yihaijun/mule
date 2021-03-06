/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.vm.functional.transactions;

import org.mule.api.transaction.TransactionCallback;
import org.mule.api.transaction.TransactionConfig;
import org.mule.module.client.MuleClient;
import org.mule.transaction.TransactionTemplate;

import javax.transaction.Transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/** Test transaction behavior when "joinExternal" is set to disallow joining external transactions
 * There is one test per legal transactional behavior (e.g. ALWAYS_BEGIN).
 */
public class NoExternalTransactionTestCase extends AbstractExternalTransactionTestCase
{

    public static final long WAIT = 3000L;

    protected static final Log logger = LogFactory.getLog(NoExternalTransactionTestCase.class);

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/config/no-external-transaction-config.xml";
    }

    @Test
    public void testBeginOrJoinTransaction() throws Exception
    {
        init();
        TransactionTemplate<String> tt = createTransactionTemplate(TransactionConfig.ACTION_BEGIN_OR_JOIN, false);

        logger.debug("TM is a " + tm.getClass().toString());
        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);

        String result;
        Exception ex = null;

        // This will throw, becasue nested transactions are not supported
        try
        {
            result = tt.execute(new TransactionCallback<String>()
            {
                public String doInTransaction() throws Exception
                {
                    return "OK";
                }
            });
        }
        catch (Exception e)
        {
            ex = e;
            logger.debug("saw exception " + e.getMessage());
        }
        assertNotNull(ex);
        tm.rollback();

        // now try with no active transaction
        result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                muleTx.enlistResource(resource1);
                resource1.setValue(15);
                return "OK";
            }
        });

        // Mule began and committed the transaction
        assertEquals(15, resource1.getPersistentValue());
    }

    @Test
    public void testBeginTransaction() throws Exception
    {
        init();
        TransactionTemplate<String> tt = createTransactionTemplate(TransactionConfig.ACTION_ALWAYS_BEGIN, false);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);

        String result;
        Exception ex = null;

        // This will throw, because nested transactions are not supported
        try
        {
            result = tt.execute(new TransactionCallback<String>()
            {
                public String doInTransaction() throws Exception
                {
                    return "OK";
                }
            });
        }
        catch (Exception e)
        {
            ex = e;
            logger.debug("saw exception " + e.getMessage());
        }
        assertNotNull(ex);
        tm.rollback();

        // now try with no active transaction
        result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                muleTx.enlistResource(resource1);
                resource1.setValue(15);
                return "OK";
            }
        });

        // Mule began and committed the transaction
        assertEquals(15, resource1.getPersistentValue());
    }

    @Test
    public void testNoTransactionProcessing() throws Exception
    {
        init();
        TransactionTemplate<String> tt = createTransactionTemplate(TransactionConfig.ACTION_NONE, false);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);

        assertNotNull(tx);
        tx.enlistResource(resource1);
        resource1.setValue(14);
        String result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNotNull(muleTx);
                return "OK";
            }
        });

        // transaction ignored, no commit
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNull(muleTx);
                return "OK";
            }
        });
    }

    @Test
    public void testAlwaysJoinTransaction() throws Exception
    {
        init();
        TransactionTemplate<String> tt = createTransactionTemplate(TransactionConfig.ACTION_ALWAYS_JOIN, false);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);

        Exception ex = null;
        String result;
        try
        {
            // Thjis will throw, because Mule sees no transaction to join
            result = tt.execute(new TransactionCallback<String>()
            {
                public String doInTransaction() throws Exception
                {
                    Transaction muleTx = tm.getTransaction();
                    assertSame(tx, muleTx);
                    resource1.setValue(14);
                    return "OK";
                }
            });
        }
        catch (Exception e)
        {
            ex = e;
            logger.debug("saw exception " + e.getMessage());
        }

        // Not committed yet, since Mule joined the external transaction
        assertNotNull(ex);
        tm.rollback();

        // try with no active transaction.. Should still throw
        try
        {
            result = tt.execute(new TransactionCallback<String>()
            {
                public String doInTransaction() throws Exception
                {
                    return "OK";
                }
            });
        }
        catch (Exception e)
        {
            ex = e;
            logger.debug("saw exception " + e.getMessage());
        }
        assertNotNull(ex);
    }

    @Test
    public void testJoinTransactionIfPossible() throws Exception
    {
        init();
        TransactionTemplate<String> tt = createTransactionTemplate(TransactionConfig.ACTION_JOIN_IF_POSSIBLE, false);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);
        String result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertSame(tx, muleTx);
                resource1.setValue(14);
                return "OK";
            }
        });

        // Not committed yet, since Mule saw no transaction to join
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        // try with no active transaction.. Should run with none
        result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNull(muleTx);
                return "OK";
            }
        });
        assertEquals("OK", result);
    }

    @Test
    public void testNoTransactionAllowed() throws Exception
    {
        init();
        TransactionTemplate<String> tt = createTransactionTemplate(TransactionConfig.ACTION_NEVER, false);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);

        // This will not throw since Mule sees no transaction
        String result = tt.execute(new TransactionCallback<String>()
        {
            public String doInTransaction() throws Exception
            {
                resource1.setValue(14);
                return "OK";
            }
        });

        // Not committed yet, since Mule saw no transaction to join
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());
    }

        /** Check that the configuration specifies considers external transactions */
    @Test
    public void testConfiguration() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        tm = client.getMuleContext().getTransactionManager();
        tm.begin();

        // This will fail, since there will be no Mule transaction to join
        client.send("vm://entry?connector=vm-normal", "OK", null);
        Object response = client.request("queue", WAIT);
        assertNull(response);

        tm.commit();
    }

}
