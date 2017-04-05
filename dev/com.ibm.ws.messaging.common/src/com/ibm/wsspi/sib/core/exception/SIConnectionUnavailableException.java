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
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite     
 * 199140           180804 gatfora  Cleanup javadoc      
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */
 package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;

/**
 This exception is thrown when attempt is made to use a connection that is not 
 available, ie when a method is invoked on a connection that has been closed. 
 The operation was not performed. The recovery action is to open a new 
 connection. 
 <p>
 This class has no security implications.
 */
public class SIConnectionUnavailableException
	extends SINotPossibleInCurrentStateException 
{

  private static final long serialVersionUID = -1542824145075787184L;
  public SIConnectionUnavailableException(String msg) {
    super(msg);
  }

  public SIConnectionUnavailableException(String msg, Throwable t) {
    super(msg, t);
  }

}
