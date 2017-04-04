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
package com.ibm.ws.sib.processor.runtime.impl;

import java.util.Iterator;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.gd.StreamSet;
import com.ibm.ws.sib.processor.gd.TargetStreamManager;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Iterates through each target stream set.
 * @author tpm100
 */
public class TargetStreamSetControllableIterator extends BasicSIMPIterator
{
  private TargetStreamManager tsm;
    
  private static TraceComponent tc =
    SibTr.register(TargetStreamSetControllableIterator.class,
      SIMPConstants.MP_TRACE_GROUP, SIMPConstants.RESOURCE_BUNDLE);

  /**
   * @param parent An iterator view of the streams in this stream set
   * @param tsm The TargetStreamManager
   */
  public TargetStreamSetControllableIterator(Iterator parent, TargetStreamManager tsm)
  {
    super(parent);
    this.tsm = tsm;
    
    if(tc.isEntryEnabled())
      SibTr.entry(tc, "TargetStreamSetControllableIterator", new Object[]{parent, tsm});
    if(tc.isEntryEnabled())
      SibTr.exit(tc, "TargetStreamSetControllableIterator", this);
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public Object next()
  {
    if(tc.isEntryEnabled())
      SibTr.entry(tc, "next");
    
    StreamSet targetStreamSet = (StreamSet)super.next();
    TargetStreamSetControl targetStreamSetControl = 
      (TargetStreamSetControl)targetStreamSet.getControlAdapter();
    if(targetStreamSetControl != null)
    {
      targetStreamSetControl.setTargetStreamManager(tsm);
    }
    
    if(tc.isEntryEnabled())
      SibTr.exit(tc, "next", targetStreamSetControl);
    return targetStreamSetControl;
  }
}
