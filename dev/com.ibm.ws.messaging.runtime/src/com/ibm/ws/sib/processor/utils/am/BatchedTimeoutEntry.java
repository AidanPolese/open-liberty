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
 * 185691           190204 tevans   Redesign/Rework LinkedMap and LockedMessageEnumeration
 * 217101           160704 sbhola   change to use SimpleLinkedList
 * 184035           231104 tevans   New MPAlarmManager for z/OS
 * 529626           160608 dware    Add cancel method to allow those interested to be told when cancelled
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.utils.am;

import com.ibm.ws.sib.processor.utils.am.BatchedTimeoutManager.LinkedListEntry;

/**
 * The entry provided to the BatchedTimeoutManager. The get and set methods are used
 * by the BatchedTimeoutManager to store data in this entry, for efficient removal.
 */
public interface BatchedTimeoutEntry
{
  public LinkedListEntry getEntry();
  public void setEntry(LinkedListEntry entry);
  public void cancel();
}
