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
 * ------------------------ -------- -----------------------------------------
 * 182068.2        21/11/03 schofiel Creation
 * 184125          15/12/03 schofiel Spill enhancements
 * 182068.3        19/12/03 schofiel Fast'n'loose - initial drop of CachedPersistentDispatcher    
 * 182068.4        06/01/04 schofiel Fast'n'loose - enhancements to dispatchers
 * 182068.5        20/01/04 schofiel Fast'n'loose - non-conflicting CachedPersistentDispatcher
 * 338397          24/01/06 gareth   Modify SpillDispatcher behaviour when full
 * SIB0112d.ms.2   28/06/07 gareth   MemMgmt: SpillDispatcher improvements - datastore
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import java.util.Collection;

import com.ibm.ws.sib.msgstore.PersistenceException;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;

/**
 * Instances of this interface persist <tt>Tasks</tt> using the persistence
 * layer. The choice of when and how to persist is left up to the implementing
 * class.
 */
public interface Dispatcher
{
    /**
     * Starts the dispatcher.
     */
    public void start();

    /**
     * Stops the dispatcher.
     *
     * @param mode specifies the type of stop operation which is to
     *             be performed.
     */
    public void stop(int mode);

    // Defect 338397
    /**
     * Used as a quick way to check the health of a dispatcher before giving it work
     * in situations in which the work cannot be rejected. For example, for a transaction
     * which requires both synchronous and asynchronous persistence, once we've done the
     * synchronous persistence, a transient persistence problem from a dispatcher will
     * not be reported from the dispatching method because we cannot guarantee to roll back the
     * synchronous work.
     * 
     * @return <tt>true</tt> if the dispatcher is experiencing no problems, else <tt>false</tt>
     */
    public boolean isHealthy();


    // SIB0112d.ms.2
    // Removed isFull() function as the file store no longer uses a dispatcher 
    // for STORE_MAYBE items. It was only in the file store case that we could 
    // reliably determine that we were full and try to do something about it.

    /**
     * Dispatches a list of <tt>Tasks</tt> to be persisted.<p>
     * The call can only be rejected in situations where the error can be
     * handled directly. Otherwise, the work must be accepted by the dispatcher
     * and coped with as well as possible.
     * 
     * @param tasks The collection of <tt>Tasks</tt> to dispatch
     * @param tran  The transaction associated with the <tt>Tasks</tt>
     * @param canReject <tt>true</tt> if the call can be rejected, <tt>false</tt> otherwise
     * 
     * @throws PersistenceException the dispatch was not accepted due to
     *     an error reported by the persistence layer
     */
    public void dispatch(Collection tasks, PersistentTransaction tran, boolean canReject) throws PersistenceException,SevereMessageStoreException;
}
