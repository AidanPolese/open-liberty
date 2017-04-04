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
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * PK67950.1        040708 egglestn Remove the initCause override (which disables initCause)
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;

/**
 This exception is thrown when an attempt is made to create a ConsumerSession 
 for a destination configured with ReceiveExclusive=true that already has a 
 consumer attached. Single-threaded applications will not typically be able to 
 recover from this condition; multi-threaded apps may be able to recover if the 
 existing consumer is part of the same application.
 <p>
 This class has no security implications.
 */
public class SIDestinationLockedException 
    extends SINotPossibleInCurrentStateException 
{
  private static final long serialVersionUID = 2348582570323085062L;

  public SIDestinationLockedException(String msg) {
    super(msg);
  }
  
}

