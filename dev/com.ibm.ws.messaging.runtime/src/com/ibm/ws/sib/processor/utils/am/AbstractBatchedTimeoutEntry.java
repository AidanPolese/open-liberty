/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.processor.utils.am;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.utils.am.BatchedTimeoutManager.LinkedListEntry;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * The entry provided to the BatchedTimeoutManager. The get and set methods are used
 * by the BatchedTimeoutManager to store data in this entry, for efficient removal.
 */
public abstract class AbstractBatchedTimeoutEntry implements BatchedTimeoutEntry
{
  //trace
  private static final TraceComponent tc =
    SibTr.register(
      AbstractBatchedTimeoutEntry.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

   
  LinkedListEntry entry;

  public LinkedListEntry getEntry()
  {
    if (tc.isEntryEnabled())
    {
      SibTr.entry(tc, "getEntry");
      SibTr.exit(tc, "getEntry", entry);
    }

    return entry;
  }

  public void setEntry(LinkedListEntry entry)
  {
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "setEntry", entry);

    this.entry = entry;

    if (tc.isEntryEnabled())
      SibTr.exit(tc, "setEntry");
  }
  
  public void cancel()
  {
    if (tc.isEntryEnabled())
      SibTr.entry(tc, "cancel");

    if (tc.isEntryEnabled())
      SibTr.exit(tc, "cancel");
  }
}
