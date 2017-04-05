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
 *                                 Version 1.3 copied from CMVC
 * ============================================================================
 */

package com.ibm.websphere.sib.exception;

/**
 * SIDataGraphFormatMismatchException is thrown when an attempt is made to interpret
 * a message payload in a format into which can the data can not be translated.
 * The recovery action for an application is to read the message in a simpler
 * format, for example as a String.
 *
 * @ibm-was-base
 * @ibm-api
 */
public class SIDataGraphFormatMismatchException extends SINotPossibleInCurrentStateException {

  private static final long serialVersionUID = -1163813360283366515L;

  public SIDataGraphFormatMismatchException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SIDataGraphFormatMismatchException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SIDataGraphFormatMismatchException(String message) {
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
  public SIDataGraphFormatMismatchException(String message, Throwable cause) {
    super(message, cause);
  }
}
