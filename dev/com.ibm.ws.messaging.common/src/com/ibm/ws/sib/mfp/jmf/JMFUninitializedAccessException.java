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
 * Exception thrown when a 'get' method of JMFMessageData attempts to access something
 *  not actually present in a message.
 */

public class JMFUninitializedAccessException extends JMFException {

  private final static long serialVersionUID = -7113228093650330453L;

  public JMFUninitializedAccessException(String reason) {
    super(reason);
  }
  public JMFUninitializedAccessException(String reason, Throwable cause) {
    super(reason, cause);
  }
}
