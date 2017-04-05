/*
 * 
 * 
 * =============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * =============================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- --------------------------------------------
 * 186445.1         200104 philip   Original
 * LIDB3706-5.187   030205 geraint  Added serialVersionUID.
 * =============================================================================
 */

package com.ibm.ws.sib.admin;

public class SIBExceptionDestinationNotFound extends SIBExceptionBase {

  private static final long serialVersionUID = 8609669081020973772L;

  public SIBExceptionDestinationNotFound(String msg) {
    super(msg);
  }

}

