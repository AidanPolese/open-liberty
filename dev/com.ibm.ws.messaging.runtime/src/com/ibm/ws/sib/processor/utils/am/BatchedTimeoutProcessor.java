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
 * 180483.3         102903 isilval  Initial implementation
 * 184035.1         110204 tevans   New MP Alarm Manager interface
 * 199574           050405 gatfora  Remove use of ArrayLists.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.utils.am;

import java.util.List;

public interface BatchedTimeoutProcessor
{
  /**
   * The method called when the timeout occurs for some entries.
   * @param timedout list of BatchedTimeoutEntry objects
   */
  public void processTimedoutEntries(List timedout);
}
