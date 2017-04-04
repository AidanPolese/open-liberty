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
 * Reason          Date   Origin   Description
 * --------        ------ -------- --------------------------------------------
 * 162584          030328 auerbach Original
 * 179681          031014 baldwint Add exception chaining constructor
 * 179681.1        040206 baldwint Ensure all exceptions have reason string
 * LIDB3706-5.229  050125 susana   Add serialVersionUID
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/**
 * Base for all JMF exceptions.
 */

public class JMFException extends Exception {

  private final static long serialVersionUID = -162901135129217885L;

  public JMFException(String reason) {
    super(reason);
  }
  public JMFException(String reason, Throwable cause) {
    super(reason, cause);
  }
}
