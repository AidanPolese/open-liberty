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
 * --------------- ----------  -----------  -----------------------------------
 *                 27/10/2003  van Leersum  Original
 * LIDB3706-5.239  19/01/2005  gareth       Add Serialization support
 * SIB0002.ms.1    28/07/2005  schofiel     Changes for remote MQ subordinate resources
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.cache.statemodel;

import com.ibm.ws.sib.transactions.PersistentTranId;

public final class TransactionIdMismatch extends StateException
{
    private static final long serialVersionUID = -4901938327580282065L;

    public TransactionIdMismatch(PersistentTranId got, PersistentTranId expected)
    {
        super("{"+got+"/"+expected+"}");
    }
}
