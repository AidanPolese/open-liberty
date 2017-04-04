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
 * SIMessageCloneFailedException is thrown if a the SIMessage clone was
 * unsuccessful for any reason.
 * <p>
 * The underlying problem is detailed in the linked Exception.
 * <p>
 * SIMessageCloneFailedException extends CloneNotSupportedException because
 * it is thrown by a clone method.
 *
 * @ibm-was-base
 * @ibm-api
 */
public class SIMessageCloneFailedException extends CloneNotSupportedException {

  private static final long serialVersionUID = 2591783634234605842L;

  /**
   * Constructor for completeness
   *
   */
  public SIMessageCloneFailedException() {
    super();
  }


  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SIMessageCloneFailedException(Throwable cause) {
    super(cause.getMessage());
  }


  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SIMessageCloneFailedException(String message) {
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
  public SIMessageCloneFailedException(String message, Throwable cause) {
    super(message + " : " + cause.getMessage());
  }


}
