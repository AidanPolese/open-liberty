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

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;

/**
 * @author cwilkin
 */
public class SIMPNotPossibleInCurrentConfigurationException
  extends SINotPossibleInCurrentConfigurationException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -214236707564245757L;

  /**
   * 
   */
  public SIMPNotPossibleInCurrentConfigurationException()
  {
    super();
  }

  /**
   * @param arg0
   */
  public SIMPNotPossibleInCurrentConfigurationException(Throwable arg0)
  {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public SIMPNotPossibleInCurrentConfigurationException(String arg0)
  {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public SIMPNotPossibleInCurrentConfigurationException(
    String arg0,
    Throwable arg1)
  {
    super(arg0, arg1);
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
