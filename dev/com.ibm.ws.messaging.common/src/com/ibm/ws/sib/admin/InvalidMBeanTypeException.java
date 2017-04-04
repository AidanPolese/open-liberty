/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *    Newly added to liberty release   051212
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import com.ibm.ws.sib.admin.SIBExceptionBase;

/**
 * @author philip
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InvalidMBeanTypeException extends SIBExceptionBase {

  private static final long serialVersionUID = 6584966515235670764L;

  public InvalidMBeanTypeException(String msg) {
    super(msg);
  }
}
