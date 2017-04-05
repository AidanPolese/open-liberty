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
 * 190632.0.3       060404 caseyj   Corruption check on createBrowserSession
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * 222183           120804 cwilkin  Add exception reasons/inserts
 * 248145           201204 gatfora  Remove code that is not used
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * 282811           270905 gatfora  Destination Corrupt should be a Resource Exception
 * ============================================================================
 */
package com.ibm.ws.sib.processor.exceptions;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 * Exception which indicates a destination was corrupt.
 */
public class SIMPDestinationCorruptException extends SIResourceException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 5443542531573242932L;
  
  /**
   * Empty constructor
   */
  public SIMPDestinationCorruptException()
  {
    super();
  }

  /**
   * @param message  The message text
   */
  public SIMPDestinationCorruptException(String message)
  {
    super(message);
  }

  /**
   * @param message  The message text.
   * @param cause  The initial exception
   */
  public SIMPDestinationCorruptException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param cause The initial exception
   */
  public SIMPDestinationCorruptException(Throwable cause)
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
