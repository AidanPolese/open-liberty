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

import com.ibm.wsspi.sib.core.SIXAResource;

/**
 * A tagging interface which identifies a transaction managed by an XAResource.
 * It allows objects returned from the TransactionFactory createXAResource*()
 * methods to be used with both the implementation and the Core SPI.
 * The peculiar name comes from (i) the desire not to reuse the interface name from
 * an existing standard (XAResource) and (ii) the peculiar way that an XAResource is
 * implemented by objects which are actually transactions too.
 */
public interface XAResourceTransaction extends TransactionCommon, SIXAResource
{
}
