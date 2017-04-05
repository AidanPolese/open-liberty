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

package com.ibm.ws.sib.processor.gd;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

public class RangeObject implements Cloneable
{
  private static TraceComponent tc =
    SibTr.register(
      RangeObject.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

    
  public long startstamp;
  public long endstamp;

  public Object clone()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry( tc, "clone", this);
    
    Object newClone = null;
    
    try
    {
      newClone = super.clone();
    }
    catch (Exception e)
    {
      // FFDC
      FFDCFilter.processException(
        e,
        "com.ibm.ws.sib.processor.gd.RangeObject.clone",
        "1:66:1.11",
        this);
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit( tc, "clone", newClone);

    return newClone;
  }
}
