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
 * 180053          31/10/03 gareth   Remove Deprecated methods/interfaces
 * 181930          17/11/03 gareth   XA Recovery Support
 * 186657.1        24/05/04 gareth   Per-work-item error checking.
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources (moved from SIB.msgstore)
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.MessageStoreException;

public interface WorkList
{
    public void addWork(WorkItem item);

    public void preCommit(PersistentTransaction transaction) throws MessageStoreException;

    public void commit(PersistentTransaction transaction) throws MessageStoreException;

    public void rollback(PersistentTransaction transaction) throws MessageStoreException;

    public void postComplete(PersistentTransaction transaction, boolean committed) throws MessageStoreException;

    public String toXmlString();
}
