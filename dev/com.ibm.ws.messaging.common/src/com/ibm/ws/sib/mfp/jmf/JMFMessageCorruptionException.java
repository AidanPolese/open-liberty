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
 * Exception thrown when a message is found to be corrupted.  The formatting service does
 * not guarantee that all forms of message corruption will be detected (a strong message
 * integrity check is the business of the security layer, not the formatting service).
 * However, there is an attempt to screen out forms of corruption that may seriously
 * compromise the functioning of the system, particularly inordinately large lengths which
 * result in the allocation of arrays large enough to cause OutOfMemory conditions.
 */

public class JMFMessageCorruptionException extends JMFException {

  private final static long serialVersionUID = -7751666081549831408L;

  public JMFMessageCorruptionException(String message) {
    super(message);
  }
  public JMFMessageCorruptionException(String message, Throwable cause) {
    super(message, cause);
  }
}
