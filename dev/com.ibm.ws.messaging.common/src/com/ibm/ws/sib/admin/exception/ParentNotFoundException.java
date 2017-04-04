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
 * 196675.2.1       190704 philip   Original
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
public class ParentNotFoundException extends SIBExceptionBase {

  private static final long serialVersionUID = 3393762560999514604L;

  public ParentNotFoundException(String msg) {
    super(msg);
  }

}
