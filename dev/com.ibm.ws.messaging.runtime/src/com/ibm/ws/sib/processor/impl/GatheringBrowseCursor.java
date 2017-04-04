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

package com.ibm.ws.sib.processor.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.interfaces.BrowseCursor;
import com.ibm.ws.sib.processor.impl.interfaces.JSConsumerManager;
import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISelectorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;

public class GatheringBrowseCursor implements BrowseCursor 
{
  /**
   * Trace for the component
   */
  private static final TraceComponent tc =
    SibTr.register(
      GatheringBrowseCursor.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

    
  List<BrowseCursor> cursors;
  
  public GatheringBrowseCursor(SelectionCriteria selectionCriteria, JSConsumerManager localCD, Map<SIBUuid8, JSConsumerManager> remoteCDs) 
    throws SISelectorSyntaxException, SIDiscriminatorSyntaxException, SIResourceException
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "GatheringBrowseCursor", 
          new Object[]{selectionCriteria, localCD, remoteCDs});
    
    cursors = new LinkedList<BrowseCursor>();
    cursors.add(localCD.getBrowseCursor(selectionCriteria));
    for (JSConsumerManager remoteCD: remoteCDs.values()) 
      cursors.add(remoteCD.getBrowseCursor(selectionCriteria));
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "GatheringBrowseCursor", this);
    
  }

  public void finished() throws SISessionDroppedException 
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "finished");
    
    Iterator<BrowseCursor> it = cursors.iterator();
    while(it.hasNext())
      it.next().finished();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "finished");
  }

  /**
   * The gathering browse will browse messages from the local itemstream as
   * a priority. If none are found it will move through the other queue points
   * until it finds a message.
   */
  public JsMessage next() throws SIResourceException, SISessionDroppedException 
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "next");
    
    JsMessage msg = null;
    Iterator<BrowseCursor> it = cursors.iterator();
    while(it.hasNext() && msg==null)
      msg = it.next().next();     
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "next");
    
    return msg;
  }

}
