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
 * SIMessageDomainNotSupportedException is thrown by the SIBus when an application
 * attempts to use a message domain not supported by the current SIBus product
 * installation. The exception message indicates the domain the application
 * attempted to use.
 *
 * @ibm-was-base
 * @ibm-api
 */
public class SIMessageDomainNotSupportedException extends SINotSupportedException {

  private static final long serialVersionUID = 6013378939342626153L;

  public SIMessageDomainNotSupportedException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SIMessageDomainNotSupportedException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SIMessageDomainNotSupportedException(String message) {
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
  public SIMessageDomainNotSupportedException(String message, Throwable cause) {
    super(message, cause);
  }
}
