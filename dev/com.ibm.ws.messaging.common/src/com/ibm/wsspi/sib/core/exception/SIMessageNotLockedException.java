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
 * 219476.0         240804 jroots   Consolidated Z3 Core SPI changes
 * 276259           130505 dware    Improve security related javadoc
 * PK67950          040708 egglestn Remove the initCause override (which disabled it)
 * ===========================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentStateException;
import com.ibm.wsspi.sib.core.SIMessageHandle;

/**
 This exception is used when a consumer attempts to read or operate on messages
 locked messages, using SIMessageHandle, but the message handle does not 
 identify a message that has been locked to the consumer. This may be because
 the message was never locked to the consumer, or it may be because the message
 lock has expired.   
 <p>
 This class has no security implications.
 */
public abstract class SIMessageNotLockedException 
    extends SINotPossibleInCurrentStateException 
{

  public SIMessageNotLockedException(String msg) {
    super(msg);
  }
  
  /**
   This method may be used to identify which messages were not processed by
   the method that threw the exception because they were not locked. 

   @return the handles of those messages that were not locked     
  */
  public abstract SIMessageHandle[] getUnlockedMessages();

}
