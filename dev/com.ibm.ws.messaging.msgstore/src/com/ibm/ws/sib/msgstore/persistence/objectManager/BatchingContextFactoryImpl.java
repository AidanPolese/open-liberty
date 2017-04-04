package com.ibm.ws.sib.msgstore.persistence.objectManager;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 *  Reason       Date     Origin     Description
 * ------------- -------- ---------- ------------------------------------------
 * SIB0003.ms.1  20/05/05  gareth    Turn on SpillDispatcher
 * 306998.20     09/01/06  gareth    Add new guard condition to trace statements
 * ============================================================================
 */

import com.ibm.ws.objectManager.ObjectManager;
import com.ibm.ws.objectManager.ObjectStore;

import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.persistence.BatchingContextFactory;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

public class BatchingContextFactoryImpl implements BatchingContextFactory
{
    private static TraceComponent tc = SibTr.register(BatchingContextFactoryImpl.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    private ObjectManager _objectManager;
    private ObjectStore   _objectStore;

    public BatchingContextFactoryImpl(ObjectManager objectManager, ObjectStore objectStore)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "<init>", "ObjectManager="+objectManager+", ObjectStore="+objectStore);

        _objectManager = objectManager;
        _objectStore   = objectStore;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "<init>");
    }

    public BatchingContext createBatchingContext()
    {
        return createBatchingContext(10, false);
    }

    public BatchingContext createBatchingContext(int capacity)
    {
        return createBatchingContext(capacity, false);
    }

    public BatchingContext createBatchingContext(int capacity, boolean useEnlistedConnections)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createBatchingContext", "Capacity="+capacity);

        BatchingContext bc = new BatchingContextImpl(_objectManager, _objectStore);
        bc.setCapacity(capacity);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createBatchingContext", "return"+bc);
        return bc;
    }

    public BatchingContext getBatchingContext(PersistentTransaction transaction, int capacity)
    {
        return getBatchingContext(transaction, capacity, false);
    }

    public BatchingContext getBatchingContext(PersistentTransaction transaction, int capacity, boolean useEnlistedConnections)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "getBatchingContext", "Transaction="+transaction+", Capacity="+capacity);

        BatchingContext bc = transaction.getBatchingContext();

        if (bc == null)
        {
            bc = createBatchingContext(capacity, useEnlistedConnections);
            transaction.setBatchingContext(bc);
        }
        else
        {
            bc.clear();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "getBatchingContext", "return="+bc);
        return bc;
    }
}
