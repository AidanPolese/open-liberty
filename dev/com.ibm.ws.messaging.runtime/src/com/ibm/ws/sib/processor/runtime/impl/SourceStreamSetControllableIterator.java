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
import com.ibm.ws.sib.processor.gd.SourceStreamManager;
import com.ibm.ws.sib.processor.gd.StreamSet;
import com.ibm.ws.sib.processor.runtime.SIMPPtoPOutboundTransmitControllable;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Iterates over the source stream sets
 * @author tpm100
 */
public class SourceStreamSetControllableIterator extends ControllableIterator
{
  private SourceStreamManager sourceStreamMgr;
    
  private static TraceComponent tc =
    SibTr.register(SourceStreamSetControllableIterator.class,
      SIMPConstants.MP_TRACE_GROUP, SIMPConstants.RESOURCE_BUNDLE);

  /**
   * @param parent An iterator view of the streams in this stream set
   * @param sourceStreamMgr The SourceStreamManager
   */
  public SourceStreamSetControllableIterator(Iterator parent, SourceStreamManager sourceStreamMgr)
  {
    super(parent);
    this.sourceStreamMgr = sourceStreamMgr;
    
    if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "SourceStreamSetControllableIterator", new Object[]{parent, sourceStreamMgr});
    if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "SourceStreamSetControllableIterator", this);
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public Object next()
  {
    if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "next");
    
    StreamSet sourceStreamSet = (StreamSet)super.next();
    SIMPPtoPOutboundTransmitControllable sourceStreamSetControl = 
      (SIMPPtoPOutboundTransmitControllable)sourceStreamSet.getControlAdapter();
    if(sourceStreamSetControl != null)
    {
      ((SourceStreamSetControl)sourceStreamSetControl).setSourceStreamManager(sourceStreamMgr);
    }
    
    if(TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "next", sourceStreamSetControl);
    return sourceStreamSetControl;
  }
  

}
