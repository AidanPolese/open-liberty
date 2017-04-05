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
 * 215547           120704 ajw      Cleanup remote runtime admin control impl
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.websphere.sib.Reliability;

/**
 * Describes information about a message that has been sent to us
 * in response to a message request
 */
public interface SIMPRequestedValueMessageInfo
{
  long getTimeout();
  
  boolean isDelivered();
  
  long getIssueTime();
  
  int getMessagePriority();
  
  Reliability getMessageReliability();
}
