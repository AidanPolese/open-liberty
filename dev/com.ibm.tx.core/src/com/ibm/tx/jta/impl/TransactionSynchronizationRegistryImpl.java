/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006, 2009 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  06-08-09  awilkins   LIDB4244  Creation                                          */
/*  07-06-06  johawkes   443467    Repackaging                                       */
/*  07-06-06  johawkes   444613    Repackaging                                       */
/*  09-06-02  mallam     596067    package move                                      */
/* ********************************************************************************* */

package com.ibm.tx.jta.impl;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import com.ibm.tx.jta.*;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.ws.Transaction.JTA.Util;

public class TransactionSynchronizationRegistryImpl implements TransactionSynchronizationRegistry
{
    private static final TraceComponent tc = Tr.register(TransactionSynchronizationRegistryImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);  

    public TransactionSynchronizationRegistryImpl()
    {
    }

    protected TransactionImpl getTransaction()
    {
        return ((TranManagerSet)TransactionManagerFactory.getTransactionManager()).getTransactionImpl();
    }
    
    public Object getTransactionKey()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getTransactionKey", this);
        
        final Object key;
        
        final TransactionImpl transaction = getTransaction();
        
        if (transaction == null)
        {
            key = null;
        }
        else
        {
            long id = transaction.getLocalId();
            key = new Long(id);
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "getTransactionKey", key);
        return key;
    }

    public void putResource(Object key, Object resource)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "putResource", new Object[]{key, resource, this});
        
        final TransactionImpl transaction = getTransaction();
        
        if (transaction == null)
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "putResource", "IllegalStateException");
            throw new IllegalStateException();          
        }
        
        transaction.putResource(key, resource); 
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "putResource");
    }

    public Object getResource(Object key)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getResource", new Object[]{key, this});
        
        final TransactionImpl transaction = getTransaction();
        
        if (transaction == null)
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "getResource", "IllegalStateException");
            throw new IllegalStateException();          
        }
        
        final Object resource = transaction.getResource(key);
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "getResource", resource);
        return resource;
    }

    public void registerInterposedSynchronization(Synchronization sync)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerInterposedSynchronization", new Object[]{sync, this});
        
        if (sync == null)
        {
            final NullPointerException npe = new NullPointerException();
            
            if (tc.isEntryEnabled()) Tr.exit(tc, "registerInterposedSynchronization", npe);
            throw npe;
        }
        
        final TransactionImpl transaction = getTransaction();
        
        if (transaction == null)
        {
            final IllegalStateException ise = new IllegalStateException();
            
            if (tc.isEntryEnabled()) Tr.exit(tc, "registerInterposedSynchronization", ise);
            throw ise;          
        }

        transaction.registerInterposedSynchronization(sync);
    }

    public int getTransactionStatus()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getTransactionStatus", this);
        
        final int status = ((TranManagerSet)TransactionManagerFactory.getTransactionManager()).getStatus();     
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "getTransactionStatus", Util.printStatus(status));
        return status;
    }

    public void setRollbackOnly()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "setRollbackOnly", this);
        
        ((TranManagerSet)TransactionManagerFactory.getTransactionManager()).setRollbackOnly();      
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "setRollbackOnly");
    }

    public boolean getRollbackOnly()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getRollbackOnly", this);             
        
        final TransactionImpl transaction = getTransaction();
        
        if (transaction == null)
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "getRollbackOnly", "IllegalStateException");
            throw new IllegalStateException();          
        }

        final boolean rollbackOnly = transaction.getRollbackOnly();
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "getRollbackOnly", rollbackOnly);
        return rollbackOnly;                
    }
}