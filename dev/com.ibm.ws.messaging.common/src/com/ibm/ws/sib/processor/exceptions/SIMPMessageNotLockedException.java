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
 * 219476.1         260804 dware    Implement latest Core SPI changes
 * 248145           201204 gatfora  Remove code that is not used
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;

import com.ibm.wsspi.sib.core.SIMessageHandle;
import com.ibm.wsspi.sib.core.exception.SIMessageNotLockedException;

public class SIMPMessageNotLockedException extends SIMessageNotLockedException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -7792307965921039274L;

  /**
   * @param arg0
   */
  public SIMPMessageNotLockedException(String arg0, SIMessageHandle[] messageHandles)
  {
    super(arg0);
    this.messageHandles = messageHandles;     
  }

  private int exceptionReason = -1;
  private String[] exceptionInserts = null;
  private SIMessageHandle[] messageHandles;

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

  /* (non-Javadoc)
   * @see com.ibm.wsspi.sib.core.exception.SIMessageNotLockedException#getUnlockedMessages()
   */
  public SIMessageHandle[] getUnlockedMessages()
  {
    return messageHandles;
  }
}
