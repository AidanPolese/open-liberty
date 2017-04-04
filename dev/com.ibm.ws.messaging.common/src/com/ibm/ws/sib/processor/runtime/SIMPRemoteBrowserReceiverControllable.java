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
 * 186484.12        090604 ajw      Finish off runtime controllable interfaces
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * 
 */
public interface SIMPRemoteBrowserReceiverControllable extends SIMPControllable
{
  /**
   * Get the unique Id of the browse session
   * 
   * @return long the browse ID
   */
  long getBrowseID();
  
  /**
   * Get the expected sequence number of the next BrowseGet message
   * 
   * @return long the sequence number
   */
  long getExpectedSequenceNumber();
}
