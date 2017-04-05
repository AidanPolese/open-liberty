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
 * 496913           270208 cwilkin  handle sendAllowed=false properly 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.exceptions;


/**
 * The SIMPSendAllowedException is thrown when the destination or message
 * point currently has SendAllowed set to false.
 */
public class SIMPSendAllowedException extends SIMPNotPossibleInCurrentConfigurationException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 3592885204172335841L;

  /**
   * Empty constructor
   */
  public SIMPSendAllowedException()
  {
    super();
  }

  /**
   * @param message  The message text
   */
  public SIMPSendAllowedException(String message)
  {
    super(message);
  }

  /**
   * @param message  The message text.
   * @param cause  The initial exception
   */
  public SIMPSendAllowedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param cause The initial exception
   */
  public SIMPSendAllowedException(Throwable cause)
  {
    super(cause);
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
