package com.ibm.ws.sib.msgstore.transactions.impl;
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
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *  326643.1       29/11/05   gareth    Use singleton objects for transaction state
 * ============================================================================
 */

public class TransactionStateCommitting2PC implements TransactionState
{
    private static final TransactionStateCommitting2PC _instance = new TransactionStateCommitting2PC();

    private static final String _toString = "TransactionStateCommitting2PC";

    static TransactionState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private TransactionStateCommitting2PC() {}

    public String toString()
    {
        return _toString;
    }
}
