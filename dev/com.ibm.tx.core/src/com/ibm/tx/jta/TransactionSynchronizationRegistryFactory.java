package com.ibm.tx.jta;

import javax.transaction.TransactionSynchronizationRegistry;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

public class TransactionSynchronizationRegistryFactory
{
    private static final TraceComponent tc = Tr.register(TransactionSynchronizationRegistryFactory.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    protected static TransactionSynchronizationRegistry _instance;

    public static synchronized TransactionSynchronizationRegistry getTransactionSynchronizationRegistry()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getTransactionSynchronizationRegistry");

        if (_instance == null)
        {
            try
            {
                final Class clazz = Class.forName("com.ibm.tx.jta.impl.TransactionSynchronizationRegistryImpl");

                _instance = (TransactionSynchronizationRegistry)clazz.newInstance();
            }
            catch(Exception e)
            {
                FFDCFilter.processException(e, "com.ibm.tx.jta.TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry", "27");
                if (tc.isEntryEnabled()) Tr.entry(tc, "getTransactionSynchronizationRegistry", e);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "getTransactionSynchronizationRegistry", _instance);
        return _instance;
    }
}