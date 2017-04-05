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
 *                                 Version 1.5 copied from CMVC
 * ============================================================================
 */

package com.ibm.websphere.sib.exception;

/**
 * A subclass of SINotPossibleInCurrentStateException is thrown by the SIBus when
 * the operation being attempted is not compatible with the current runtime state
 * of the system.
 * <p>
 * The specific subclass indicates the error and the recovery action appropriate.
 *
 * @ibm-was-base
 * @ibm-api
 */
public abstract class SINotPossibleInCurrentStateException extends SIException {
  public SINotPossibleInCurrentStateException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SINotPossibleInCurrentStateException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SINotPossibleInCurrentStateException(String message) {
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
  public SINotPossibleInCurrentStateException(String message, Throwable cause) {
    super(message, cause);
  }
}
