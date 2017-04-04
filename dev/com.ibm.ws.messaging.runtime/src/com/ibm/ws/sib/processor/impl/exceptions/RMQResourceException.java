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
 * 347967           160206 tevans   Send msg to exception dest if PEV fails
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.exceptions;

import com.ibm.ws.sib.processor.exceptions.SIMPResourceException;

public class RMQResourceException extends SIMPResourceException
{
  private static final long serialVersionUID = 7937371154987272905L;

  /**
   * 
   */
  public RMQResourceException()
  {
    super();
  }

  /**
   * @param arg0
   */
  public RMQResourceException(Throwable arg0)
  {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public RMQResourceException(String arg0)
  {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public RMQResourceException(String arg0, Throwable arg1)
  {
    super(arg0, arg1);
  }
}
