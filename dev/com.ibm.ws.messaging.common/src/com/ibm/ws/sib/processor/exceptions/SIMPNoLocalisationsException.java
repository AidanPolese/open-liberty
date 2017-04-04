/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5639-D57, 5630-A36, 5630-A37, Copyright IBM Corp. 2012
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
 * 175637.2.27      250304 millwood  SIMPNoLocalisationsException creation.
 * 222183           120804 cwilkin  Add exception reasons/inserts
 * 248145           201204 gatfora  Remove code that is not used
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * SIB0113b.mp.1    040907 dware    Initial support for SIB0113b function (moved)
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.exceptions;


/**
 * The SIMPNoLocalisationsException is thrown when we are left with a 
 * destination that has no addressible localisations. For example, when
 * a scoping alias (SIB0113) incorrectly identifies an ME that does not
 * localise the target destination.
 */
public class SIMPNoLocalisationsException extends SIMPNotPossibleInCurrentConfigurationException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 3592885204172335841L;

  /**
   * Empty constructor
   */
  public SIMPNoLocalisationsException()
  {
    super();
  }

  /**
   * @param message  The message text
   */
  public SIMPNoLocalisationsException(String message)
  {
    super(message);
  }

  /**
   * @param message  The message text.
   * @param cause  The initial exception
   */
  public SIMPNoLocalisationsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param cause The initial exception
   */
  public SIMPNoLocalisationsException(Throwable cause)
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
