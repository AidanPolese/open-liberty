/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version 1.4 copied from CMVC
 * ============================================================================
 */

package com.ibm.websphere.sib.exception;

/**
 * A subclass of SIInvalidParameterException is thrown by the SIBus when a
 * parameter to a method is not valid, and the validation rules are non-trivial.
 * <p>
 * The specific subclass indicates the parameter in error and the
 * validation failure.
 *
 * @ibm-was-base
 * @ibm-api
 */
public abstract class SIInvalidParameterException extends SIException {
  public SIInvalidParameterException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SIInvalidParameterException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SIInvalidParameterException(String message) {
    super(message);
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy and additional information is
   * to be included.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SIInvalidParameterException(String message, Throwable cause) {
    super(message, cause);
  }
}
