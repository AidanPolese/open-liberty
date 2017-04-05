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
 * ---------------  ------ -------- -------------------------------------------
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 199140           180804 gatfora  Cleanup javadoc      
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */

package com.ibm.wsspi.sib.core;

import javax.transaction.xa.XAResource;

/**
 SIXAResource may be used to enlist a Jetstream Messaging Engine into XA 
 transactions. A started XAResource can be passed on send and receive methods to 
 enlist the underlying operations into the transaction.
 <p>
 This class has no security implications.
*/
public interface SIXAResource extends XAResource, SITransaction {

  /**
   Returns true if the SIXAResource object is currently enlisted in a 
   transaction.
   
   @return true if the object is currently enlisted in a transaction
  */
  public boolean isEnlisted();
	
}

