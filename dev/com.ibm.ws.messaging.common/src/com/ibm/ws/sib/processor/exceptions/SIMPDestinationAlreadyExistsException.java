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
 * 161135           170303 tevans   Original
 * 162915           080403 tevans   Make the Core API code look like the model
 * 166828           060603 tevans   Core MP rewrite
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 222183           120804 cwilkin  Add exception reasons/inserts
 * 248145           201204 gatfora  Remove code that is not used
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;

import com.ibm.websphere.sib.exception.SIException;

/**
 SIMPDestinationAlreadyExistsException is thrown when an attempt is made to create an
 object with the same identity (as defined by the semantics of the object type)
 as an object that already exists. For example, if you attempt to create a 
 durable subscription using the name of an existing durable subscription, 
 SIMPDestinationAlreadyExistsException is thrown.
 * @modelguid {8B4C163F-C8B3-4AED-8396-7304E8165835}
*/
public class SIMPDestinationAlreadyExistsException extends SIException {

  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 4431970614227288988L;
	
  /** @modelguid {EC9EE62C-F763-440C-98DB-16769DAD3DA6} */
  public SIMPDestinationAlreadyExistsException() {
    super();
  }
  
  /** @modelguid {9C0AD404-9AAB-4FDD-9D0A-336D5241FE2C} */
  public SIMPDestinationAlreadyExistsException(String msg) {
    super(msg);
  }
  	
  /** @modelguid {442B74FC-7D95-410E-ADDD-5B5CBE73DAE7} */
  public SIMPDestinationAlreadyExistsException(Throwable t) {
    super(t);
  }
  
  /** @modelguid {A1A47589-9142-41B5-8225-5C65A97F8DDE} */
  public SIMPDestinationAlreadyExistsException(String msg, Throwable t) {
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

