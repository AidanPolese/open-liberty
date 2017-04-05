/**
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
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 286811          22-Sep-05 pnickoll New class to allow the CF to distinguish between failures caused by clone connection failing and other exceptions
 * ============================================================================
 */
package com.ibm.ws.sib.ra.impl;

import javax.resource.ResourceException;

public class SibRaCloneConnectionException extends ResourceException {

  public SibRaCloneConnectionException (String s) {
    super (s);
  }
  
  public SibRaCloneConnectionException (Throwable th) {
    super (th);
  }
  
  public SibRaCloneConnectionException (String s, Throwable th) {
    super (s, th);
  }
  
  public SibRaCloneConnectionException (String s1, String s2) {
    super (s1, s2);
  }
  
}
