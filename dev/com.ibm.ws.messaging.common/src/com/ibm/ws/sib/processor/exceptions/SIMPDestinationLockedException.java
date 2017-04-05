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
 * 264822           120405 gatfora  Original
 * 309940           091005 gatfora  Remove compile warnings.
 * ============================================================================
 */
 package com.ibm.ws.sib.processor.exceptions;

import com.ibm.wsspi.sib.core.exception.SIDestinationLockedException;

/**
 * @author gatfora
 *
 * Class used to provide custom SIDestinationLockedException properties
 */
public class SIMPDestinationLockedException extends
    SIDestinationLockedException
{
  /**
   * 
   */
  private static final long serialVersionUID = 4296862552917302653L;
  
  public static final int CONSUMERS_ATTACHED = 0;
  public static final int UNCOMMITTED_MESSAGES = 1;
  
  private int _type;

  /**
   * @param arg0
   */
  public SIMPDestinationLockedException(String arg0)
  {
    super(arg0);
  }
  
  /**
   * @param arg0
   * @param type  The reason that this destination is locked.
   */
  public SIMPDestinationLockedException(String arg0, int type)
  {
    super(arg0);
    
    _type = type;
  }
  
  public int getType()
  {
    return _type;
  }
}
