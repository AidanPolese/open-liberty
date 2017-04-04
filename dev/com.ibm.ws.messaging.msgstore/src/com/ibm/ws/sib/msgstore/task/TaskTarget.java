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
 * Reason          Date        Origin       Description
 * --------------- ------      --------     --------------------------------------------
 *                 180804      van Leersum  Original
 * SIB0002.ms.1    28/07/05    schofiel     Changes for remote MQ subordinate resources
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.task;

import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;

public interface TaskTarget {
    public abstract void abortAdd(final PersistentTransaction transaction);

    public abstract void abortPersistLock(PersistentTransaction transaction);
    public abstract void abortPersistUnlock(PersistentTransaction transaction);
    public void abortRemove(final PersistentTransaction transaction) ;
    public abstract void abortUpdate(final PersistentTransaction transaction);
    public abstract void commitAdd(final PersistentTransaction transaction);
    public abstract void commitPersistLock(PersistentTransaction transaction);
    public abstract void commitPersistUnlock(PersistentTransaction transaction);
    public void commitRemove(final PersistentTransaction transaction) ;
    public abstract void commitUpdate(final PersistentTransaction transaction);
    public abstract void postAbortAdd(final PersistentTransaction transaction);
    public abstract void postAbortPersistLock(PersistentTransaction transaction);

    public abstract void postAbortPersistUnlock(PersistentTransaction transaction);
    public void postAbortRemove(final PersistentTransaction transaction);

    public abstract void postAbortUpdate(final PersistentTransaction transaction);
    public abstract void postCommitAdd(final PersistentTransaction transaction);
    public abstract void postCommitPersistLock(PersistentTransaction transaction);

    public abstract void postCommitPersistUnlock(PersistentTransaction transaction);

    public void postCommitRemove(final PersistentTransaction transaction);
    public abstract void postCommitUpdate(final PersistentTransaction transaction);
    public abstract void preCommitAdd(final PersistentTransaction transaction);
    public void preCommitRemove(final PersistentTransaction transaction) ;
    public abstract void preCommitUpdate(final PersistentTransaction transaction);
}
