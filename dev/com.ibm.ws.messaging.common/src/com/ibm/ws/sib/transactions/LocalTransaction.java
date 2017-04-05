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
 * SIB0002.tran.2   030805 tevans   Integration with MP
 * ===========================================================================
 */
package com.ibm.ws.sib.transactions;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.wsspi.sib.core.SIUncoordinatedTransaction;

/**
 * A tagging interface which identifies an auto-commit transaction.
 * It allows objects returned from the TransactionFactory createLocalCommitTransaction*()
 * methods to be used with both the implementation and the Core SPI.
 */
public interface LocalTransaction extends TransactionCommon, SIUncoordinatedTransaction
{
    public void begin() throws SIIncorrectCallException;
}
