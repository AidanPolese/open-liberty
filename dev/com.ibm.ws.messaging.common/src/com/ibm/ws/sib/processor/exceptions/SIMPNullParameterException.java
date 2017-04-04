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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 159093           070303 jroots   Original
 * 162915           080403 tevans   Make the Core API code look like the model
 * 166828           060603 tevans   Core MP rewrite
 * 249157           071204 gatfora  Removal of WsException useage.
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;


/**
 * @author jroots
 */
public class SIMPNullParameterException extends RuntimeException {
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -6818408987309669258L;

  public SIMPNullParameterException(String msg) {
    super(msg);
  }
}
