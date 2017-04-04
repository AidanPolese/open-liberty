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
 * 252277.2        060110 susana   Original
* ============================================================================
 */

package com.ibm.ws.sib.mfp.impl;

/**
 * MFPUnsupportedEncodingRuntimeException is thrown when MQ Message Encapsulation
 * code needs to percolate an UnsupportedEncodingException up through the JMF
 * layers (which don't expect it) so that we can ultimately throw a suitable
 * Exception to the end user.
 * <p>
 * The UnsupportedEncodingException is passed in as the cause.
 * Whenever this exception is thrown it MUST be caught at a higher-level of MFP
 * code and the UnsupportedEncodingException extracted and thrown on.
 */
public class MFPUnsupportedEncodingRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MFPUnsupportedEncodingRuntimeException() {
    super();
  }

  /**
   * Constructor for when the Exception is to be thrown because another
   * Exception has been caught.
   *
   * @param cause The original Throwable which has caused this to be thrown.
   */
  public MFPUnsupportedEncodingRuntimeException(Throwable cause) {
    super(cause);
  }

}
