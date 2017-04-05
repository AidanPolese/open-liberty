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

/**
 This subclass of SIConnectionUnavailableException is thrown when the connection 
 was closed by the bus, typically as a result of a Comms failure. (Note: this 
 exception is not used to indicate that a comms failure occurred during the 
 execution of the method invoked; SIConnectionLostException is used for that 
 purpose, although SIConnectionDroppedException will be thrown on subsequent 
 invocations.)
 <p>
 This class has no security implications.
 */
public class SIConnectionDroppedException
	extends SIConnectionUnavailableException 
{
  private static final long serialVersionUID = 8704309876012415509L;
  
  public SIConnectionDroppedException(String msg) {
    super(msg);
  }

  public SIConnectionDroppedException(String msg, Throwable t) {
    super(msg, t);
  }

}
