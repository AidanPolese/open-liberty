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

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.MessageProcessor;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;
import com.ibm.ws.sib.processor.impl.store.items.MessageItem;
import com.ibm.ws.sib.processor.runtime.SIMPLinkReceivedMessageControllable;
import com.ibm.ws.sib.processor.utils.SIMPUtils;
import com.ibm.ws.sib.utils.ras.SibTr;

public class LinkReceivedMessageControl extends ReceivedMessage implements
    SIMPLinkReceivedMessageControllable {

  private static final TraceComponent tc =
    SibTr.register(
      LinkReceivedMessageControl.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);


  public LinkReceivedMessageControl(MessageItem item, MessageProcessor msgProcessor, DestinationHandler destHandler) {
    super(item, msgProcessor, destHandler);
    // TODO Auto-generated constructor stub
  }
  
  public String getTargetDestination() {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getTargetDestination");
    
    String destination= null;
    
    try 
    {
      destination = getJsMessage().getRoutingDestination().getDestinationName();
    } catch(Exception e)
    {
      FFDCFilter.processException(
        e,
        "com.ibm.ws.sib.processor.runtime.LinkReceivedMessageControl.getTargetDestination",
        "1:68:1.2",
        this);    
        
      SibTr.error(tc, "INTERNAL_MESSAGING_ERROR_CWSIP0002",
        new Object[] { "com.ibm.ws.sib.processor.runtime.LinkReceivedMessageControl.getTargetDestination", 
                       "1:73:1.2", 
                       SIMPUtils.getStackTrace(e) }); 
      SibTr.exception(tc, e); 
    } 
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getTargetDestination", destination);      
    return destination;
  }

  public String getTargetBus() {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getTargetBus");
    
    String bus= null;
    
    try 
    {
      bus = getJsMessage().getRoutingDestination().getBusName();
    } catch(Exception e)
    {
      FFDCFilter.processException(
        e,
        "com.ibm.ws.sib.processor.runtime.LinkReceivedMessageControl.getTargetBus",
        "1:97:1.2",
        this);    
        
      SibTr.error(tc, "INTERNAL_MESSAGING_ERROR_CWSIP0002",
        new Object[] { "com.ibm.ws.sib.processor.runtime.LinkReceivedMessageControl.getTargetBus", 
                       "1:102:1.2", 
                       SIMPUtils.getStackTrace(e) }); 
      SibTr.exception(tc, e); 
    } 
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getTargetBus", bus);      
    return bus;
  }

}
