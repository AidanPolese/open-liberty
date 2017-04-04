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
 *                                 Version 1.6 copied from CMVC
 * ============================================================================
 */
package com.ibm.websphere.sib.exception;

/**
 * This checked exception is thrown whenever a method call is not incorrect.
 *
 * For example, if an argument is not allowed to be null and null is passed,
 * or if the value of the argument is incorrect, then this exception is thrown.
 * It also thrown if the object on which the method is invoked is inappropriate
 * (for example, if an attempt is made to commit a completed transaction).
 * In any case where the validation rules are non-trivial, or considered likely to change,
 * a specific subclass is thrown, otherwise SIIncorrectCallException is used directly.
 * An SIIncorrectCallException (or its subclasses) should not contain a linked exception.
 *
 * @ibm-was-base
 * @ibm-api
 */
public class SIIncorrectCallException extends SIException
{

  private static final long serialVersionUID = 8626401808281400097L;

  public SIIncorrectCallException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SIIncorrectCallException(Throwable cause) {
    super(cause);
  }
  
  /**
   * Constructor for when the Exception
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SIIncorrectCallException(String message) {
    super(message);
  }

}
