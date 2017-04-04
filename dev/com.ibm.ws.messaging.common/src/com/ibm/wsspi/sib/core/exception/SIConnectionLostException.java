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
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */
package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 This exception is thrown whenever a Comms error occurs during the execution of 
 a method, resulting in the SICoreConnection being closed. Any subsequent 
 attempt to use the lost connection will result in SIConnectionDroppedException. 
 This exception indicates that the outcome of the operation is unknown. 
 <p>
 This class has no security implications.
 */
public class SIConnectionLostException extends SIResourceException 
{

  private static final long serialVersionUID = -9095554106264670028L;
  public SIConnectionLostException(String msg) {
    super(msg);
  }

  public SIConnectionLostException(String msg, Throwable t) {
    super(msg, t);
  }

}
