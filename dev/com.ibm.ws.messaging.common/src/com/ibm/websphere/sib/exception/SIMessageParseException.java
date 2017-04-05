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
 * SIMessageParseException is used when the system needs to throw an
 * unchecked exception to indicate a parse failure. The other (checked)
 * SIException classes should be used where possible.
 * <p>
 * This exception does not use any new reason code or inserts, so it inherits
 * the SIRCConstants.SIRC0001_DELIVERY_ERROR behaviour from SIErrorException.
 * @ibm-was-base
 * @ibm-api
 */
public class SIMessageParseException extends SIErrorException {

  private static final long serialVersionUID = -5177496595904095444L;


  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught.
   *
   * @param cause The original Throwable which has caused this to be
   * thrown.
   */
  public SIMessageParseException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the parse.
   *
   * @param message A String giving information about the problem which caused
   * this to be thrown.
   */
  public SIMessageParseException(String message) {
    super(message);
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy and additional information
   * is to be included.
   *
   * @param message A String giving information about the problem which
   * caused this to be thrown.
   * @param cause The original Throwable which has caused this to be
   * thrown.
   */
  public SIMessageParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
