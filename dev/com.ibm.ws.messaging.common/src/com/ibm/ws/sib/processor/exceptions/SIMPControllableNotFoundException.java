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
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;


public class SIMPControllableNotFoundException extends SIMPException {
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -2286782716163250456L;

  public SIMPControllableNotFoundException(String msg) {
    super(msg);
  }
  
  public SIMPControllableNotFoundException(Throwable t) {
    super(t);
  }
}
