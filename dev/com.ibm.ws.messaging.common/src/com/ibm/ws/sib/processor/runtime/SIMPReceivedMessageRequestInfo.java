/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Material
 * 
 * IBM WebSphere
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
 * 248030.1         170105 tpm      MBean extensions
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.wsspi.sib.core.SelectionCriteria;

/**
 * Information about a message request that we have received from a remote ME
 * @author tpm100
 */
public interface SIMPReceivedMessageRequestInfo extends SIMPControllable
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
