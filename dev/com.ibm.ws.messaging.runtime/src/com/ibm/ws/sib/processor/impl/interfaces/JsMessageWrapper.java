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
 * ---------------  ------ -------- ------------------------------------------
 * 409469           281206 tevans   Refactor LME
 * SIB0163.mp.4     081107 nyoung   Support the driving of Message Events.
 * SIB0113a.mp.9    091107 cwilkin  Gathering Consumers
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.transactions.TransactionCallback;

public interface JsMessageWrapper extends TransactionCallback
{
  /**
   * Get the underlying message object
   *
   * @return The underlying message object 
   */
  public JsMessage getMessage();

  public int guessRedeliveredCount();

  public boolean isReference();

  public Object getReportCOD() throws SIResourceException;

  public long updateStatisticsMessageWaitTime();
  
  public long getMessageWaitTime();
  
  /**
   * Whether this message was obtained via remote get
   */
  public boolean isRemoteGet();
}
