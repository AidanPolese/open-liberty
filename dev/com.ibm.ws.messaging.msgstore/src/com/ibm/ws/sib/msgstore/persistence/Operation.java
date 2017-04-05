package com.ibm.ws.sib.msgstore.persistence;
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
 *                 18/11/03 pradine  Original
 * 180763.7        10/02/04 pradine  Add support for mutiple item tables
 * 188052          10/03/04 pradine  Changes to the garbage collector
 * 188052.1        16/03/04 schofiel Remove deprecated persist() method
 * 188050.4        06/04/04 pradine  SpecJAppServer2003 optimization
 * 201701.1        21/05/04 pradine  Optimise use of collection classes
 * 213328          30/06/04 pradine  Perform synchronous delete during 2PC processing
 * 214205          06/07/04 schofiel Clean up size calculations for tasks
 * 247513          14/01/05 schofiel Improve spilling performance
 * 272110          10/05/05 schofiel 602:SVT: Malformed messages bring down the AppServer
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          23/07/08 susana   Use getInMemorySize for spilling & persistence
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.PersistenceException;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.transactions.impl.TransactionState;

/**
 * These are operations that must be provided by the Cache layer to the
 * Persistence layer.
 */
public interface Operation 
{
    /**
     * Returns a {@link Persistable} object for processing.
     * 
     * @return a {@link Persistable} object
     */
    public Persistable getPersistable();

    /**
     * Returns the approximate in memory size of {@link Persistable}.
     * 
     * @param tranState the state of the transaction associated with the operation
     * @return the size
     */
    public int getPersistableInMemorySizeApproximation(TransactionState tranState);
    
    /**
     * This is the mechanism via which a {@link Persistable} that carries the data
     * related to a write operation is passed to the Persistence layer for processing.
     * 
     * @param bc        the {@link BatchingContext} via which the {@link Persistable} will be serialized to the database.
     * @param tranState the state of the transaction associated with the operation
     */
    public void persist(BatchingContext bc, TransactionState tranState);
    
    /**
     * The data in a {@link Persistable} is copied in order to preserve its state
     * in case it may be overwritten. A copy is not required provided that the data
     * is immutable.
     *
     */
    public void copyDataIfVulnerable() throws PersistenceException, SevereMessageStoreException;
    
    /**
     * Ensures that the data in a {@link Persistable} is available for writing by the
     * {@link Operation} irrespective of timing considerations with respect to the
     * actual time of the write.
     *
     */
    public void ensureDataAvailable() throws PersistenceException, SevereMessageStoreException;
    
    /**
     * Indicates whether the operation to be performed will create a persistent
     * representation in the database.
     * 
     * @return <code>true</code> if it is a create (insert) operation, <code>false</code> otherwise.
     */
    public boolean isCreateOfPersistentRepresentation();
    
    /**
     * Indicates whether the operation to be performed will delete a persistent
     * representation in the database.
     * 
     * @return <code>true</code> if it is a delete operation, <code>false</code> otherwise.
     */
    public boolean isDeleteOfPersistentRepresentation(); 
}
