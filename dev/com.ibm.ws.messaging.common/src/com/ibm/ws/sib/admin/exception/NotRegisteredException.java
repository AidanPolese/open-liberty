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
 * 196675.1.1       040504 philip   Original
 * LIDB3706-5.187   030205 geraint  Added serialVersionUID.
 * =============================================================================
 */

package com.ibm.ws.sib.admin.exception;

import com.ibm.ws.sib.admin.SIBExceptionBase;

/**
 * @author philip
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class NotRegisteredException extends SIBExceptionBase {

  private static final long serialVersionUID = -1746562860973306382L;

  public NotRegisteredException(String msg) {
    super(msg);
  }
}
