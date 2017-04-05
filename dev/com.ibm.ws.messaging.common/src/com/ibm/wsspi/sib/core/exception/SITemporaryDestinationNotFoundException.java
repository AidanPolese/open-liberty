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
 * PK67950.1        040708 egglestn Remove the initCause override (which disables initCause)
 * ============================================================================
 */
 package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;

/**
 This exception is thrown when a specified temporary destination is not recognised.
 
 This class has no security implications.
 */
public class SITemporaryDestinationNotFoundException
  extends SINotPossibleInCurrentStateException 
{

  private static final long serialVersionUID = 5752938072435741910L;
  public SITemporaryDestinationNotFoundException(String msg) {
    super(msg);
  }

}
