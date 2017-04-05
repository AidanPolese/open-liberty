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
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */
 package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;

/**
 This exception is thrown when attempt is made to use a destination session that 
 is not available, for example when a method is invoked on a session that has 
 been closed, or when createBifurcatedConsumerSession is invoked on an id that 
 does not correspond to a live consumer. 
 <p>
 This class has no security implications.
 */
public class SISessionUnavailableException
	extends SINotPossibleInCurrentStateException 
{

  private static final long serialVersionUID = 887189624894252171L;
  public SISessionUnavailableException(String msg) {
    super(msg);
  }

  public SISessionUnavailableException(String msg, Throwable t) {
    super(msg, t);
  }

}
