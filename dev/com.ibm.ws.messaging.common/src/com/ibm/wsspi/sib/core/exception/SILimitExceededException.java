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

import com.ibm.websphere.sib.exception.SIResourceException;

/**
 This exception is thrown when an architected Jetstream limit prevents the 
 method from completing. The exception message should describe the specific 
 limit that would be exceeded were the method to be allowed to complete 
 normally, and give the configured value of the limit. SILimitExceededException 
 should not contain a linked exception. The application may choose to retry the 
 method call in the hope that resources have become available. 
 <p>
 This class has no security implications.
 */
public class SILimitExceededException extends SIResourceException 
{

  private static final long serialVersionUID = -9020374047684536734L;
  public SILimitExceededException(String msg) {
    super(msg);
  }

}
