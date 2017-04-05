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
 * --------------- ------ -------- --------------------------------------------
 * 168606          030609 susana   Original
 * LIDB3706-5.235  050124 susana   Add serialVersionUID
 * SIB0121a.mfp.1  070423 susana   Implement Reasonable by extending AbstractMfpException
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

/**
 * IncorrectMessageTypeException is thrown if the type of the message was not
 * as expected so processing can not continue.
 * <p>
 * The Exception text details the expected and actual message types.
 */
public class IncorrectMessageTypeException extends AbstractMfpException {

  private static final long serialVersionUID = 9037856092705261428L;

  /**
  /**
   * Constructor for completeness
   *
   */
  public IncorrectMessageTypeException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public IncorrectMessageTypeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public IncorrectMessageTypeException(String message) {
    super(message);
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught and additional information is to be included.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public IncorrectMessageTypeException(String message, Throwable cause) {
    super(message, cause);
  }

}
