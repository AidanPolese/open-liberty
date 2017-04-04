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
 * 186484.10.1      170504 tevans   Warm restart MBean Registration
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;


public class SIMPInvalidRuntimeIDException extends SIMPException 
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -35962071282560025L;

  public SIMPInvalidRuntimeIDException(String msg) {
    super(msg);
  }
  
  public SIMPInvalidRuntimeIDException(Throwable t) {
    super(t);
  }
  
  public SIMPInvalidRuntimeIDException(String msg, Throwable t) {
    super(msg, t);
  }
}
