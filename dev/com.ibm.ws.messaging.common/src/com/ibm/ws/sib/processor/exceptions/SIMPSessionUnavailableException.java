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
 * 222183           120804 cwilkin  Extend core spi exceptions to include reasonCodes/inserts
 * 248145           201204 gatfora  Remove code that is not used
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.exceptions;

import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

public class SIMPSessionUnavailableException
  extends SISessionUnavailableException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -4526334613982155271L;

  /**
   * @param msg
   */
  public SIMPSessionUnavailableException(String msg)
  {
    super(msg);
  }

  /**
   * @param msg
   * @param t
   */
  public SIMPSessionUnavailableException(String msg, Throwable t)
  {
    super(msg, t);
  }
  
  private int exceptionReason = -1;
  private String[] exceptionInserts = null;

  /* (non-Javadoc)
   * @see com.ibm.websphere.sib.exception.SIException#getExceptionInserts()
   */
  public String[] getExceptionInserts()
  {
    if (exceptionInserts == null)
      return super.getExceptionInserts();
    
    return exceptionInserts;
  }

  /* (non-Javadoc)
   * @see com.ibm.websphere.sib.exception.SIException#getExceptionReason()
   */
  public int getExceptionReason()
  {
    if (exceptionReason < 0)
      return super.getExceptionReason();
    
    return exceptionReason;
  }
  
  /**
   * Set the exception inserts
   * @param exceptionInserts
   */
  public void setExceptionInserts(String[] exceptionInserts)
  {
    this.exceptionInserts = exceptionInserts;
  }
  
  /**
   * Set the exception reason code
   * @param exceptionReason
   */
  public void setExceptionReason(int exceptionReason)
  {
    this.exceptionReason = exceptionReason;
  }

}
