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
package com.ibm.ws.sib.processor.impl.indexes.statemodel;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Class to represent the various lookups for different destination types
 */ 
public class CleanupPending extends Visible
{
  /** 
   * Trace for the component
   */
  private static final TraceComponent tc =
    SibTr.register(
      CleanupPending.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
 
  
  public boolean isCleanupPending()
  {
    return true;
  }

  public State cleanupComplete()
  {
    return State.ACTIVE; 
  }

  public State create()
  {
    return this; 
  }
  
  public State defer()
  {
    return State.CLEANUP_DEFERED; 
  }

  public State delete()
  {
    return State.DELETE_PENDING; 
  }
  
  public State cleanup()
  {
    return this; 
  }

  public String toString()
  {
    return "CLEANUP_PENDING";
  }
}
