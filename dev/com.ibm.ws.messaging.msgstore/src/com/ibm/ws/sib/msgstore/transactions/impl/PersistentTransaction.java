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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 164655          06/05/03 gareth   Initial Transaction code drop
 * 182347          11/11/03 gareth   Add Persistent Transaction ID
 * 183471          04/12/03 gareth   Remove old PersistenceManager interfaces
 * 186559          07/01/04 gareth   Add COMMITTING_2PC state
 * 184806.3.2      15/04/04 gareth   Move getPersistentTranId()
 * 229186          03/09/04 pradine  Cache the batching context inside a transaction
 * 229486          07/05/04 gareth   Improve javadoc
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 326643.1        29/11/05 gareth   Use singleton objects for transaction state
 * 410652          12/04/07 gareth   Check Transactions ME at add time
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.MessageStore;
import com.ibm.ws.sib.msgstore.ProtocolException;
import com.ibm.ws.sib.msgstore.TransactionException;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.transactions.Transaction;

/**
 * This interface contains the utility methods used by the persistence layer when
 * processing the work contained in a transaction branch and the constants used
 * by those methods.
 */
public interface PersistentTransaction extends Transaction
{
    /**
     * Zero phase auto-commit transaction.
     */
    public static final int TX_AUTO_COMMIT = 0;
    /**
     * Resource manager local transaction
     */
    public static final int TX_LOCAL = 1;
    /**
     * XA compliant global transaction
     */
    public static final int TX_GLOBAL = 2;

    /**
     * Associates a {@link BatchingContext} with the transaction
     * so that if it is reused then there is no need to allocate
     * a new {@link BatchingContext}
     * 
     * @param bc the batching context to associate with the transaction
     */
    public void setBatchingContext(BatchingContext bc);
    
    /**
     * Retrieves the {@link BatchingContext} that was previously
     * associated with the transaction.
     * 
     * @return the batching context, or <code>null</code> if no batching context
     *         has been assigned
     */
    public BatchingContext getBatchingContext();

    /**
     * Determines what type of transaction the implementing object is a participant in.
     * 
     * @return {@link #TX_AUTO_COMMIT}
     *         <BR>{@link #TX_LOCAL}
     *         <BR>{@link #TX_GLOBAL}
     */
    public int getTransactionType();

    /**
     * Sets the current state of the implementing participant.
     * 
     * @param state  One of the following:
     *               <BR>{@link TransactionState#TRAN_STATE_ACTIVE}
     *               <BR>{@link TransactionState#TRAN_STATE_PREPARING}
     *               <BR>{@link TransactionState#TRAN_STATE_PREPARED}
     *               <BR>{@link TransactionState#TRAN_STATE_COMMITTING_1PC}
     *               <BR>{@link TransactionState#TRAN_STATE_COMMITTING_2PC}
     *               <BR>{@link TransactionState#TRAN_STATE_COMMITTED}
     *               <BR>{@link TransactionState#TRAN_STATE_ROLLINGBACK}
     *               <BR>{@link TransactionState#TRAN_STATE_ROLLEDBACK}
     *               <BR>{@link TransactionState#TRAN_STATE_NONE}
     */
    public void setTransactionState(TransactionState state);

    /**
     * Determines the current state of the implementing participants transaction.
     * 
     * @return One of the following:
     *         <BR>{@link TransactionState#TRAN_STATE_ACTIVE}
     *         <BR>{@link TransactionState#TRAN_STATE_PREPARING}
     *         <BR>{@link TransactionState#TRAN_STATE_PREPARED}
     *         <BR>{@link TransactionState#TRAN_STATE_COMMITTING_1PC}
     *         <BR>{@link TransactionState#TRAN_STATE_COMMITTING_2PC}
     *         <BR>{@link TransactionState#TRAN_STATE_COMMITTED}
     *         <BR>{@link TransactionState#TRAN_STATE_ROLLINGBACK}
     *         <BR>{@link TransactionState#TRAN_STATE_ROLLEDBACK}
     *         <BR>{@link TransactionState#TRAN_STATE_NONE}
     */
    public TransactionState getTransactionState();

    /**
     * Adds an individual piece of work to the unit-of-work that this transaction
     * object represents.
     * 
     * @param item   The work item to add.                   
     * 
     * @exception ProtocolException
     *                   Thrown if an add is attempted when the transaction cannot allow any
     *                   further work to be added i.e. after completion of the transaction.
     * @exception TransactionException
     *                   Thrown if an unexpected error occurs.
     */
    public void addWork(WorkItem item) throws ProtocolException, TransactionException;


    // Defect 178563
    /**
     * Allows the messageStore internals to access the task list for this 
     * Transaction to proces the contents of the unit-of-work.
     * 
     * @return This transactions task list
     */
    public WorkList getWorkList();

    // Defect 410652
    /**
     * This method is used to check the MessageStore instance that an implementing
     * transaction object originated from. This is used to check that a transaction
     * is being used to add Items to the same MessageStore as that it came from.
     * 
     * @return The MessageStore instance where this transaction originated from.
     */
    public MessageStore getOwningMessageStore();
}
