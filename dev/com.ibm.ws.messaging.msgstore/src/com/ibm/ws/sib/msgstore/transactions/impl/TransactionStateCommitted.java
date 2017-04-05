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

public class TransactionStateCommitted implements TransactionState
{
    private static final TransactionStateCommitted _instance = new TransactionStateCommitted();

    private static final String _toString = "TransactionStateCommitted";

    static TransactionState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private TransactionStateCommitted() {}

    public String toString()
    {
        return _toString;
    }
}
