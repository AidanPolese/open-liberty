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
 * 216685           180704 ajw      cleanup anycast runtime admin impl
 * 229588           070904 gatfora  Remove the removed getIdentifier use
 * 452517           210807 cwilkin  Remove formatState
 * 461986           240807 sibcopyr Automatic update of trace guards 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.AOBrowserSession;
import com.ibm.ws.sib.processor.runtime.SIMPRemoteBrowserReceiverControllable;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This object represnts a remote browse of this local queue. 
 */
public class RemoteBrowserReceiver implements SIMPRemoteBrowserReceiverControllable
{
  private static TraceComponent tc =
    SibTr.register(
      RemoteBrowserReceiver.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
  
  // The browserSession that this object represents.    
  private AOBrowserSession aoBrowserSession;
  
  public RemoteBrowserReceiver(AOBrowserSession aoBrowserSession)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "RemoteBrowserReceiver", aoBrowserSession);
      
    this.aoBrowserSession = aoBrowserSession;
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "RemoteBrowserReceiver", this);
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPRemoteBrowserReceiverControllable#getBrowseID()
   */
  public long getBrowseID()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getBrowseID");
      
    long browseID = aoBrowserSession.getKey().getBrowseId();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getBrowseID", new Long(browseID));
    return browseID;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPRemoteBrowserReceiverControllable#getExpectedSequenceNumber()
   */
  public long getExpectedSequenceNumber()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getExpectedSequenceNumber");
      
    long expectedSeqNumber = aoBrowserSession.getExpectedSequenceNumber();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getExpectedSequenceNumber", new Long(expectedSeqNumber));
    return expectedSeqNumber;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.SIMPControllable#getName()
   */
  public String getName()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.Controllable#getId()
   */
  public String getId()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getId");
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getId", aoBrowserSession.toString());
    return aoBrowserSession.toString();
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.Controllable#getConfigId()
   */
  public String getConfigId()
  {    
    return null;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.Controllable#getRemoteEngineUuid()
   */
  public String getRemoteEngineUuid()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getRemoteEngineUuid");
      
    String remoteMEId = aoBrowserSession.getKey().getRemoteMEUuid().toString();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getRemoteEngineUuid", remoteMEId);
    return remoteMEId;
  }

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.admin.Controllable#getUuid()
   */
  public String getUuid()
  {
    return null;
  }

}
