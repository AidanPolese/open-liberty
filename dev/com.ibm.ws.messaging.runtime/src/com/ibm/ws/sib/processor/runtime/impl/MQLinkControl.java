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
 * SIB0105.mp.6     210607 nyoung   MQLink Controllable changes 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

import com.ibm.websphere.ras.TraceComponent;

import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.BaseDestinationHandler;
import com.ibm.ws.sib.processor.impl.MessageProcessor;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * The MQLinkControl operates as an extension to the Queue controllable rather than the
 * VirtualLinkControl. The overridden getState() method allows access to the range of 
 * Link states.
 */
public class MQLinkControl extends Queue
{
  private static final TraceComponent tc =
  SibTr.register(
    MQLinkControl.class,
    SIMPConstants.MP_TRACE_GROUP,
    SIMPConstants.RESOURCE_BUNDLE);

  /* Output source info */
  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      SibTr.debug(tc, "Source info: 1:47:1.1");
  }
  
  public MQLinkControl(MessageProcessor messageProcessor,
                              BaseDestinationHandler destination)
  {
    super(messageProcessor, destination);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(
        tc,
        "MQLinkControlAdapter",
        new Object[] { messageProcessor, destination});
      SibTr.exit(tc, "MQLinkControlAdapter", this);
    }
  }
  
  /* (non-Javadoc)
   * @see com.ibm.ws.sib.processor.runtime.MediatedMessageHandlerControl#getState()
   */
  public String getState()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getState"); 
    String state = messageProcessor.getDestinationManager().getLinkIndex().getState(baseDest).toString();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getState", state); 
    return state;
  }
}
