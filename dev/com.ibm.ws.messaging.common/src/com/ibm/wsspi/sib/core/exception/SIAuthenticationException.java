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
 * PK67950.1        040708 egglestn Remove the initCause override (which disables initCause)
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIException;

/**
 This exception is thrown if the credentials supplied on the createConnection 
 call are not accepted. Instances of this exception should not contain a linked 
 exception. 
 <p>
 This class has no security implications.
*/
public class SIAuthenticationException extends SIException {
	 
  private static final long serialVersionUID = -458588735063364784L;
  
  public SIAuthenticationException(String msg) {
    super(msg);
  } 

}

