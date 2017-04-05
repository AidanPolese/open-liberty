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
 * Reason        Date     Origin       Description
 * ------------- -------- ------------ ------------------------------------------
 * 168081        01/09/03  gareth      GlobalTransaction Support (Local Clients)
 * 181930        12/11/03  gareth      XA Recovery Support
 * 188054.1      16/03/04  gareth      Enhanced JDBC Exception handling
 * 229486        07/05/04  gareth      Improve javadoc
 * PM31431       02/02/11  slaterpa    Prevent unresolved indoubts
 * ============================================================================
 */
                           
import com.ibm.ws.sib.msgstore.*;

/**
 * This interface holds all of the methods used by the transaction code to complete
 * work that has been carried out as part of the implementing Transaction object.
 */
public interface TransactionParticipant extends PersistentTransaction
{
    /**
     * The first stage of completion in the two-phase-commit protocol. Once this method has 
     * returned the participant should know whether it is capable of successfully committing 
     * the work associated with it. If successful completion is possible then a vote of XA_OK 
     * is returned. If completion is not possible an exception is thrown.
     * 
     * @return The Prepare vote for this transaction:
     *         <BR>XAResource.XA_OK - Participant is ready to commit its work successfully.
     *         <BR>XAResource.XA_RDONLY - Participant has only done reads as part of this
     *         transaction so it has no work to commit.
     * @exception SeverePersistenceException
     *                   Thrown if a serious exception occured in the persistence
     *                   layer. To ensure no further loss of data integrity the
     *                   ME will be brought down.
     * @exception ProtocolException
     *                   This method was called at the wrong time during the transaction
     *                   completion process.
     * @exception RollbackException
     *                   The work done by this participant has been rolled back due
     *                   to a problem.
     * @exception TransactionException
     *                   An unexpected exception occurred processing the transaction.
     */
    public int prepare() throws ProtocolException, RollbackException, SeverePersistenceException, TransactionException;


    /**
     * The second stage of completion in the two-phase-commit protocol. Once this method has
     * returned all work associated with this transaction participant should be complete. If
     * the onePhase parameter is true then the one-phase completion protocol will be used
     * and commit can be called without a preceding call to prepare.
     * 
     * @param onePhase Determines whether this commit action is one-phase
     *                 or two-phase. If two-phase then a previously successful
     *                 prepare call must have been made.
     * 
     * @exception SeverePersistenceException
     *                   Thrown if a serious exception occured in the persistence
     *                   layer. To ensure no further loss of data integrity the
     *                   ME will be brought down.
     * @exception PersistenceException
     *                   Thrown if an exception occurred in the persistence layer
     *                   that could be recovered from or that may dissapear over
     *                   time. e.g. network problem.
     * @exception ProtocolException
     *                   This method was called at the wrong time during the transaction
     *                   completion process.
     * @exception RollbackException
     *                   The work done by this participant has been rolled back due
     *                   to a problem.
     * @exception TransactionException
     *                   An unexpected exception occurred processing the transaction.
     */
    public void commit(boolean onePhase) throws ProtocolException, RollbackException, SeverePersistenceException, 
                                                TransactionException, PersistenceException;


    /**
     * Aborts all work associated with this transaction participant. Once this method has
     * returned all changes carried out as part of this transaction should be undone.
     * 
     * @exception SeverePersistenceException
     *                   Thrown if a serious exception occured in the persistence
     *                   layer. To ensure no further loss of data integrity the
     *                   ME will be brought down.
     * @exception PersistenceException
     *                   Thrown if an exception occurred in the persistence layer
     *                   that could be recovered from or that may dissapear over
     *                   time. e.g. network problem.
     * @exception ProtocolException
     *                   This method was called at the wrong time during the transaction
     *                   completion process.
     * @exception TransactionException
     *                   An unexpected exception occurred processing the transaction.
     */
    public void rollback() throws ProtocolException, SeverePersistenceException, TransactionException, 
                                  PersistenceException;


    /**
     * Outputs an XML representation of this TransactionParticipant and 
     * its associated work.
     * 
     * @return The XML String.
     */
    public String toXmlString();
}

