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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0002.tran.2   290705 schofiel Integration with MS
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.transactions;

import com.ibm.ws.sib.transactions.LocalTransaction;

/**
 * This interface is implemented by local transactions which can be used with the
 * Message Store.
 */
public interface ExternalLocalTransaction extends LocalTransaction, Transaction
{
}
