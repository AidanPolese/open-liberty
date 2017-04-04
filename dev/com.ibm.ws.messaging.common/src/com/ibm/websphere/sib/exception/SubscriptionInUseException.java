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
 *                                 Version 1.2 copied from CMVC
 * ============================================================================
 */

package com.ibm.websphere.sib.exception;

import com.ibm.ws.sib.admin.SIBExceptionBase;

/**
 * @author philip
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SubscriptionInUseException extends SIBExceptionBase {

  private static final long serialVersionUID = -804785414761373888L;

  public SubscriptionInUseException(String msg) {
    super(msg);
  }

}

