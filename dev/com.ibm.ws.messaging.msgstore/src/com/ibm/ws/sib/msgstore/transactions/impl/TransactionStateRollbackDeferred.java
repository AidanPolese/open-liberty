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
 *  PK81848        09/03/09   pbroad    Add transaction state for deferred rollback
 *  586978         23/04/09   djvines   Make _toString static
 * ============================================================================
 */

public class TransactionStateRollbackDeferred implements TransactionState
{
    private static final TransactionStateRollbackDeferred _instance = new TransactionStateRollbackDeferred();

    private static final String _toString = "TransactionStateRollbackDeferred";

    static TransactionState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only
     * be accessed via instance method.
     */
    private TransactionStateRollbackDeferred() {}

    public String toString()
    {
        return _toString;
    }
}

