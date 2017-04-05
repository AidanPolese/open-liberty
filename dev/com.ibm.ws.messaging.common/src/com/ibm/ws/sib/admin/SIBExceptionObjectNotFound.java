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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- ---------------------------------------------
 * 191793.2        090304 philip   Initial ForeignBus related interfaces
 * LIDB3706-5.187  030205 geraint  Added serialVersionUID.
 * =============================================================================
 */
package com.ibm.ws.sib.admin;

/**
 * @author philip
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SIBExceptionObjectNotFound extends SIBExceptionBase {

  private static final long serialVersionUID = -2346917706418005372L;

  public SIBExceptionObjectNotFound(String msg) {
    super(msg);
  }

}
