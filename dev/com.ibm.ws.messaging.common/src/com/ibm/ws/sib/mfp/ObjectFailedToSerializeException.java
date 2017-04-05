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
 * SIB0212.mfp.1   061211 mphillip creation
 * SIB0121a.mfp.1  070423 susana   Use correct Exception Reason & Insert values
 * SIB0121.mfp.5   070629 susana   Shrink class hierarchy
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import com.ibm.websphere.sib.SIRCConstants;
import com.ibm.websphere.sib.exception.SIMessageException;

/**
 * Exception to be thrown when the object in a message payload can not be serialized.
 */
public class ObjectFailedToSerializeException extends SIMessageException {

  private static final long serialVersionUID = 1L;

  private final static int REASON = SIRCConstants.SIRC0200_OBJECT_FAILED_TO_SERIALIZE;
  private String[] inserts;

  public ObjectFailedToSerializeException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   * @param className The name of the class which can not be serialized,
   */
  public ObjectFailedToSerializeException(Throwable cause, String className) {
    super(cause);
    inserts = new String[1];
    inserts[0] = className;
  }

  /**
   * @see com.ibm.ws.sib.utils.Reasonable#getExceptionReason()
   * @return a reason code that can be used if this exception causes a message
   *         to be rerouted to the exception destination
   */
  public int getExceptionReason() {
    return REASON;
  }

  /**
   * @see com.ibm.ws.sib.utils.Reasonable#getExceptionInserts()
   * @return a set of inserts (that can be inserted into the message corresponding exception reason) if
   *         this exception causes a message to be rerouted to the exception destination
   */
  public String[] getExceptionInserts() {
    return inserts;
  }

}
