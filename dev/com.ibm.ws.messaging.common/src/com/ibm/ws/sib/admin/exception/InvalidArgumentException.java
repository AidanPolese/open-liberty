
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

package com.ibm.ws.sib.admin.exception;

import com.ibm.ws.sib.admin.SIBExceptionBase;
public class InvalidArgumentException extends SIBExceptionBase {

  private static final long serialVersionUID = -2999284360684254691L;

  public InvalidArgumentException(String msg) {
    super(msg);
  }

}



