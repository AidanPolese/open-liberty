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
 *  Reason           Date      Origin     Description
 * --------------- --------- ---------- ----------------------------------------
 *  PM31431        02/02/11   slaterpa    New state
 * ============================================================================
 */

public class TransactionStateRollbackExpected implements TransactionState
{
    /** A state to represent the XA specs idle state before transitioning 
     * to rolled back.  This state is required when we fail to prepare, and
     * we fail to rollback (facilitating XA_RB response to prepare).  RM_FAIL
     * is returned instead, and we are simply in a state where we are waiting
     * for a rollback.  Rollback is retried and on the first rollback call,
     * state transitions to RollingBack.
     */
     
    
    private static final TransactionStateRollbackExpected _instance = new TransactionStateRollbackExpected();

    private static final String _toString = "TransactionStateRollbackExpected";

    static TransactionState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private TransactionStateRollbackExpected() {}

    public String toString()
    {
        return _toString;
    }
}
