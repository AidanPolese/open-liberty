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
 This subclass of SISessionUnavailableException is used to indicate that the 
 DestinationSession has been pre-emptively closed by the bus, for example 
 because the destination to which it was attached has been deleted, or following 
 a change of the SendAllowed or ReceiveAllowed value. The exception message 
 should indicate exactly what caused the session to be closed. The application 
 may try to recreate the session in the hope that such a state change has been 
 reversed.
 <p>
 This class has no security implications.
 */
public class SISessionDroppedException extends SISessionUnavailableException {

  private static final long serialVersionUID = 3517552638648582598L;
  public SISessionDroppedException(String msg) {
    super(msg);
  }

  public SISessionDroppedException(String msg, Throwable t) {
    super(msg, t);
  }

}
