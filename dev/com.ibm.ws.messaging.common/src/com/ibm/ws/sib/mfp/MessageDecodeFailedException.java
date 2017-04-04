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
 * 166631          030519 susana   Original
 * LIDB3706-5.235  050124 susana   Add serialVersionUID
 * SIB0121a.mfp.1  070423 susana   Implement Reasonable by extending AbstractMfpException
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

/**
 * MessageDecodeFailedException is thrown if the component is unable
 * to decode the Message.
 * <p>
 * An FFDC record will already have been written detailing what could not be
 * decoded. This probably means that the encoded buffer has been corrupted.
 */
public class MessageDecodeFailedException extends AbstractMfpException {

  private static final long serialVersionUID = -3066014842348692041L;

  /**
   * Constructor for completeness
   *
   */
  public MessageDecodeFailedException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public MessageDecodeFailedException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public MessageDecodeFailedException(String message) {
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
  public MessageDecodeFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
