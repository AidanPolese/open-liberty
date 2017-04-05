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
 * 188197           050204 mcobbett Original
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * PK67950.1        040708 egglestn Remove the initCause override (which disables initCause)
 * ===========================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;

/**
 This is an SIIncorrectCallException thrown on createTemporaryDestination if the 
 destinationPrefix is not valid. It is necessary because the validation is 
 non-trivial, as evidenced by SICoreUtils.isDestinationPrefixValid. 
 <p>
 This class has no security implications.
 */
public class SIInvalidDestinationPrefixException extends SIIncorrectCallException
{

  private static final long serialVersionUID = 6712576188966498101L;

  public SIInvalidDestinationPrefixException(String msg)
  {
    super(msg);
  }

}
