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
 * 162915           080403 tevans   Make the Core API code look like the model
 * 166828           060603 tevans   Core MP rewrite
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * 337346           110106 gatfora  Remove unmappable chars.
 * PK67950.1        250708 egglestn Remove the initCause override (which disabled it)
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;

/**
 Those clients, such as the JMS API layer, that have a requirement to 
 discriminate permission failures from other config errors (such as "destination 
 not found", can do so by catching this subclass of 
 SINotPossibleInConfigurationException. Other clients are encouraged to avoid 
 leaking information to unauthorized end users by just catching the parent 
 exception.
 <p>
 This class has no security implications.
*/
public class SINotAuthorizedException 
    extends SINotPossibleInCurrentConfigurationException
{
	
  private static final long serialVersionUID = -3528137123062357016L;
  public SINotAuthorizedException(String msg) {
    super(msg);
  }
  
}

