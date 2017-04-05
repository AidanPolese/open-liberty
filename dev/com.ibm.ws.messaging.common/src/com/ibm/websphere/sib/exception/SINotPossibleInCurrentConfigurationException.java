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
 * SINotPossibleInCurrentConfigurationException is thrown by the SIBus when
 * the operation being attempted can not be performed because the current runtime
 * configuration does not permit it.
 * <p>
 * The exception message describes the precise way in which the operation is
 * incompatible with the configuration.
 *
 * @ibm-was-base
 * @ibm-api
 */
public class SINotPossibleInCurrentConfigurationException extends SIException {

  private static final long serialVersionUID = 4790393311934744169L;

  public SINotPossibleInCurrentConfigurationException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught during the copy.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public SINotPossibleInCurrentConfigurationException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught during the copy.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public SINotPossibleInCurrentConfigurationException(String message) {
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
  public SINotPossibleInCurrentConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
