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
 * 167779          030603 baldwint Original
 * 179681          031014 baldwint Add exception chaining constructor
 * 179681.1        040206 baldwint Ensure all exceptions have reason string
 * LIDB3706-5.229  050125 susana   Add serialVersionUID
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/**
 * Exception thrown when trying to register a JMFSchema if its generated id appears
 * to clash with an existing but different schema.  Since these ids are 64bit ids
 * derived from an SHA-1 hashcode on the schema defintion clashes are not expected
 * to occur.
 *
 * If clashes do occur and the schema ids are being generated as zero, it is likely
 * that the JVM properties are incorrectly configured and the SHA-1 security
 * provider cannot be found.
 */

public class JMFSchemaIdException extends JMFException {

  private final static long serialVersionUID = 3879104843122112156L;

  public JMFSchemaIdException(String message) {
    super(message);
  }
  public JMFSchemaIdException(String message, Throwable cause) {
    super(message, cause);
  }
}
