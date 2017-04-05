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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 190632.0.20      170604 caseyj   SIMPMessageProcessorControllable:resetMH() 
 * LIDB3706-5.247  180105 gatfora  Include a serialVersionUid for all serializable objects
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.exceptions;

import com.ibm.websphere.sib.exception.SIErrorException;

/**
 * @author caseyj
 *
 * This exception should be thrown when code attempts to execute a method on 
 * an interface which is invalid for the particular implementation referenced.
 * <p>
 * It is a runtime exception so that it will cause unit test errors without
 * needing try/catching, but can be caught if it is expected.
 */
public class InvalidOperationException extends SIErrorException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 2005588020713364517L;

  /**
   * Empty constructor
   */
  public InvalidOperationException()
  {
    super();
  }

  /**
   * @param message  The message text
   */
  public InvalidOperationException(String message)
  {
    super(message);
  }

  /**
   * @param message  The message text.
   * @param cause  The initial exception
   */
  public InvalidOperationException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param cause The initial exception
   */
  public InvalidOperationException(Throwable cause)
  {
    super(cause);
  }
}
