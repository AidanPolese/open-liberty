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
 * 227424           270804 isl      Ordering context changes
 * 248030.1         170105 tpm      MBean extensions
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.wsspi.sib.core.SelectionCriteria;

/**
 * Object containing information about a message request that we have sent
 * to a remote ME
 */
public interface SIMPRequestMessageInfo extends SIMPControllable
{
  long getIssueTime();
  
  long getTimeout();
  
  SelectionCriteria[] getCriterias();
  
  long getACKingDME();
  
  /**
   * @return a long for the time of completion/expiration of this 
   * message.
   * SIMPConstants.INFINITE_TIMEOUT if this message does not expire
   * @author tpm
   */
  long getCompletionTime();
}
