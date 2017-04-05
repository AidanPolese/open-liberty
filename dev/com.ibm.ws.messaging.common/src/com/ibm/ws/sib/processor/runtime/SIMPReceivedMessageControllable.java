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
 * 260627.1         230305  tpm      Creation
 * 316556           251005  gatfora  Should use exported processor package for State Strings
 * ============================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.SIMPConstants;

/**
 * The inbound receivers' view of received messages
 * @author tpm
 */
public interface SIMPReceivedMessageControllable extends SIMPRemoteMessageControllable
{
  
  public static class State
  {
    /** 
     * The message has been received but has not yet been delivered
     */
    public static final String AWAITING_DELIVERY = SIMPConstants.AWAITINGDEL_STRING;
  }
  
  
  public long getSequenceID();
  
  public long getPreviousSequenceID();
 
}
