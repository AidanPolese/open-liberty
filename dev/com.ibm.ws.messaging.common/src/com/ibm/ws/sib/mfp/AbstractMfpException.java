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
 * SIB0121a.mfp.1  070423 susana   Original
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.sib.utils.Reasonable;

/**
 * AbstractMfpException is a base exception class which all Mfp Exceptions extend
 * unless either they alredy extend another SIB Exception, or they are a psuedo-MQ
 * Exception.
 * The class only exists in order to common up the code for implementing Reasonable.
 */
public abstract class AbstractMfpException extends Exception implements Reasonable {

  /**
  /**
   * Constructor for completeness
   *
   */
  public AbstractMfpException() {
    super();
  }


  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public AbstractMfpException(Throwable cause) {
    super(cause);
  }


  /**
   * Constructor for when the Exception is to be thrown for a reason other than
   * that an Exception has been caught.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   */
  public AbstractMfpException(String message) {
    super(message);
  }


  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught and additional information is to be included.
   *
   * @param message A String giving information about the problem which caused this to be thrown.
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public AbstractMfpException(String message, Throwable cause) {
    super(message, cause);
  }


  /**
   * @see com.ibm.ws.sib.utils.Reasonable#getExceptionReason()
   * @return a reason code that can be used if this exception causes a message
   *         to be rerouted to the exception destination
   */
  public int getExceptionReason() {
    Throwable cause = getCause();

    if (cause instanceof Reasonable)
      return ((Reasonable)cause).getExceptionReason();
    else if (cause instanceof SIException)
      return ((SIException)cause).getExceptionReason();
    else if (cause instanceof SIErrorException)
      return ((SIErrorException)cause).getExceptionReason();
    else
      return Reasonable.DEFAULT_REASON;
  }

  /**
   * @see com.ibm.ws.sib.utils.Reasonable#getExceptionInserts()
   * @return a set of inserts (that can be inserted into the message corresponding exception reason) if
   *         this exception causes a message to be rerouted to the exception destination
   */
  public String[] getExceptionInserts() {
    Throwable cause = getCause();

    if (cause instanceof Reasonable)
      return ((Reasonable)cause).getExceptionInserts();
    else if (cause instanceof SIException)
      return ((SIException)cause).getExceptionInserts();
    else if (cause instanceof SIErrorException)
      return ((SIErrorException)cause).getExceptionInserts();
    else
      return Reasonable.DEFAULT_INSERTS;
  }

}
