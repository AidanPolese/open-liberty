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
 * ---------------  ------ -------- -------------------------------------------
 * SIB0009.core.01  150805 rjnorris Add invokeCommand() to CoreConnection
 * D310654          021105 mattheg  Remove initCause() restriction
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIException;

/**
 This exception is thrown by the invokeCommand method if the invoked command
 returns an exception. The original exception is returned as a linked exception. 
 <p>
 This class has no security implications.
*/
public class SICommandInvocationFailedException extends SIException 

{
  private static final long serialVersionUID = 2224385837581092786L;
  
  public SICommandInvocationFailedException(String msg)
  {
    super(msg);
  }
  
  public SICommandInvocationFailedException(String msg, Throwable linkedException)
  {

    super(msg, linkedException);
  }
}
