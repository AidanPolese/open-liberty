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
 * Exception thrown when a the JMFSchema that would be required to make a call to the
 * JMFI correct is not the actual JMFSchema in use in the context of the call.
 */

public class JMFSchemaViolationException extends JMFException {

  private final static long serialVersionUID = -351451162120378552L;

  public JMFSchemaViolationException(String message) {
    super(message);
  }
  public JMFSchemaViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}
