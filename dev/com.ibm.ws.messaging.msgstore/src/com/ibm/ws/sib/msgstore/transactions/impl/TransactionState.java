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
 *  PK81848        09/03/09   pbroad    Add deferred rollback state
 *  590648         20/05/09   pbroad    Back-out PK81848
 *  PK81848.1      18/06/09   pbroad    Re-fix PK81848 taking into account additional requirements from 590648
 *  PM31431        02/02/11   slaterpa  Add rollback expected state
 * ============================================================================
 */

 
/**
 * This class is a parent class for transaction states. It holds 
 * a set of singleton instances of named transaction state objects
 * that are used instead of an int/String so that state can be 
 * checked in a heap dump by looking at the object type.
 */
public interface TransactionState
{
    /**
     * Transaction participant has been created.
     */
    public static final TransactionState STATE_ACTIVE = TransactionStateActive.instance();
    /**
     * Transaction participant has begun prepare processing.
     */
    public static final TransactionState STATE_PREPARING = TransactionStatePreparing.instance();
    /**
     * Transaction participant has completed prepare processing and returned its prepare vote.
     */
    public static final TransactionState STATE_PREPARED = TransactionStatePrepared.instance();
    /**
     * Transaction participant has begun completion using the one-phase protocol.
     */
    public static final TransactionState STATE_COMMITTING_1PC = TransactionStateCommitting1PC.instance();
    /**
     * Transaction participant has begun the second phase of completion using the two-phase protocol.
     */
    public static final TransactionState STATE_COMMITTING_2PC = TransactionStateCommitting2PC.instance();
    /**
     * Transaction participant has completed all work associated with it.
     */
    public static final TransactionState STATE_COMMITTED = TransactionStateCommitted.instance();
    /**
     * Transaction participant has begun rolling back all of its associated work.
     */
    public static final TransactionState STATE_ROLLINGBACK = TransactionStateRollingBack.instance();
    /**
     * Transaction participant has determined a rollback outcome for the transaction, but it must be actioned by another thread that has placed the txn in STATE_PREPARING 
     */
    public static final TransactionState STATE_ROLLBACK_DEFERRED = TransactionStateRollbackDeferred.instance();
    /**
     * Transaction participant has completed rolling back all of its associated work.
     */
    public static final TransactionState STATE_ROLLEDBACK = TransactionStateRolledBack.instance();
    /**
     * Transaction particpant is not currently associated with a transaction.
     */
    public static final TransactionState STATE_NONE = TransactionStateNone.instance();
    /**
     * Transaction participant failed to prepare and failed to rollback inline.
     * Awaiting rollback from Transaction Service.
     */
    public static final TransactionState STATE_ROLLBACK_EXPECTED = TransactionStateRollbackExpected.instance();
}
