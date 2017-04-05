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
 * 160138          030307 susana   Original
 * LIDB3706-5.235  050124 susana   Add serialVersionUID
 * SIB0121a.mfp.1  070423 susana   Implement Reasonable by extending AbstractMfpException
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

/**
 * MessageCopyFailedException is thrown if the component is unable
 * to make a safe copy of a Message for send or receive.
 * <p>
 * The Exception text details why the copy failed. This is probably an
 * internal error.
 */
public class MessageCopyFailedException extends AbstractMfpException {

  private static final long serialVersionUID = -4658482005767282204L;

  /**
  /**
   * Constructor for completeness
   *
   */
  public MessageCopyFailedException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public MessageCopyFailedException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public MessageCopyFailedException(String message) {
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
  public MessageCopyFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
