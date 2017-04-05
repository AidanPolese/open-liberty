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
 * 192759           090304 jroots   Milestone 7 Core SPI changes
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * 276259           130505 dware    Improve security related javadoc
 * PK67950.1        040708 egglestn Remove the initCause override (which disables initCause)
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;

/**
 This exception is thrown by the createDurableSubscription method if a durable 
 subscription already exists with the name given. It should not contain a linked 
 exception. The recovery action in this case is either to attach to the existing 
 subscription, or to delete it.
 <p>
 This class has no security implications.
 */
public class SIDurableSubscriptionAlreadyExistsException
  extends SINotPossibleInCurrentStateException
{

  private static final long serialVersionUID = -7722841239140825361L;
  public SIDurableSubscriptionAlreadyExistsException(String msg)
  {
    super(msg);
  }

}
