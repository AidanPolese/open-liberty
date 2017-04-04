package com.ibm.ws.sib.msgstore.task;
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
 *                 26/06/03 drphill  Original
 * 180053          03/11/03 gareth   Remove deprecated methods/interfaces
 * 180763.5        21/11/03 pradine  Add support for new PersistenceManager Interface
 * 188052          10/03/04 pradine  Changes to the garbage collector
 * 188052.1        16/03/04 schofiel Remove deprecated persist() method
 * 213328          30/06/04 pradine  Perform synchronous delete during 2PC processing
 * 214205          06/07/04 schofiel Clean up size calculations for tasks
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          23/07/08 susana   Use getInMemorySize for spilling & persistence
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;
import com.ibm.ws.sib.msgstore.transactions.impl.TransactionState;

/**
 * Task to trigger an auto-commit transaction even when we don't need anything done....
 */
public class NullTask extends Task
{
    public NullTask(AbstractItemLink link) throws SevereMessageStoreException {super(link);}

    public int getPersistableInMemorySizeApproximation(TransactionState tranState) {return 0;}

    public void abort(PersistentTransaction transaction) {}

    public void commitExternal(PersistentTransaction transaction) {}

    public void commitInternal(PersistentTransaction transaction) {}

    public void persist(BatchingContext batchingContext, TransactionState transtate) {}

    public void postAbort(PersistentTransaction transaction) {}

    public void postCommit(PersistentTransaction transaction) {}

    public void preCommit(PersistentTransaction transaction) {}
}
