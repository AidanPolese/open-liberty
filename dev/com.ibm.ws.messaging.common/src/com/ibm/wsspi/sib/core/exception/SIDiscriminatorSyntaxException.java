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
 * 173765.0         110803 jroots   Original
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * PK67950.1        040708 egglestn Remove the initCause override (which disables initCause)
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;

/**
 This is an SIIncorrectCallException thrown on createTemporaryDestination if 
 the destinationPrefix is not valid. It is necessary because the validation is 
 non-trivial, as evidenced by SICoreUtils.isDestinationPrefixValid. 
 <p>
 This class has no security implications.
 */
public class SIDiscriminatorSyntaxException
	extends SIIncorrectCallException 
{
		
  private static final long serialVersionUID = -3588496744460913800L;
  public SIDiscriminatorSyntaxException(String msg) {
    super(msg);
  }

}
