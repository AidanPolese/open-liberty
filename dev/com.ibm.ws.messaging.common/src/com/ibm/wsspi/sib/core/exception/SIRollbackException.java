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
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 This exception is thrown by the commit method if the transaction is rolled 
 back. The transaction object can no longer be used, and a new transaction must 
 be created. 
 <p>
 This class has no security implications.
 */
public class SIRollbackException extends SIResourceException 
{
	
  private static final long serialVersionUID = -2378844529328141711L;
  public SIRollbackException(String msg) {
    super(msg);
  }
  
  public SIRollbackException(String msg, Throwable t) {
    super(msg, t);
  }

}

