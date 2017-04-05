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
 * ---------------  ------ -------- ------------------------------------------
 * SIB0163.mp.5     221107 nyoung   Exploit new MFP MessageControlClassification property
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.exceptions;

public class InvalidFlowsException extends Exception
{
  private static final long serialVersionUID = 3283070569718292269L;

  /**
   * InvalidFlowsException is thrown when the set of flows that are being applied to a ConsumerSet are invalid.
   */
  public InvalidFlowsException()
  {
    super();
  }

  /**
   * InvalidFlowsException is thrown when the set of flows that are being applied to a ConsumerSet are invalid.
   *
   * @param arg0  Exception text
   */
  public InvalidFlowsException(String arg0)
  {
    super(arg0);
  }
}
