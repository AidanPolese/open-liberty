/*
 * 
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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 558352           281008 cwilkin  Retry failed attach to avoid deadlock
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl.exceptions;

import com.ibm.websphere.sib.exception.SIResourceException;


public class SIMPNoResponseException extends SIResourceException 
{

  /**
   * 
   */
  private static final long serialVersionUID = 5298893702048228363L;

  public SIMPNoResponseException() {
    super();
  }

  public SIMPNoResponseException(String message, Throwable cause) {
    super(message, cause);
  }

  public SIMPNoResponseException(String message) {
    super(message);
  }

  public SIMPNoResponseException(Throwable cause) {
    super(cause);
  }

}
