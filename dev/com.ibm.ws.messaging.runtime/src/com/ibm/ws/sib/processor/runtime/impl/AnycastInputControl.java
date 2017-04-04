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
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.gd.AIStream;
import com.ibm.ws.sib.processor.impl.AnycastInputHandler;
import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.ws.sib.utils.ras.SibTr;

public class AnycastInputControl 
{

  private static final TraceComponent tc =
    SibTr.register(
      AnycastInputControl.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  private AnycastInputHandler aih;

  public AnycastInputControl(AnycastInputHandler aih) 
  {    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "AnycastInputControl", aih);
    
    this.aih = aih;
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "AnycastInputControl", this);
  }
  
  public AIStream getStream()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getStream");
    
    AIStream stream = aih.getAIStream();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getStream", stream);
    
    return stream;
  }
  
  public long getNumberOfCompletedRequests()
  {    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getNumberOfCompletedRequests");
   
    long requests = aih.getCompletedRequestCount();
   
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getNumberOfCompletedRequests", Long.valueOf(requests));
    
    return requests;
  }
  
  public SIBUuid8 getDMEUuid()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.entry(tc, "getDMEUuid");
    
    SIBUuid8 uuid = aih.getLocalisationUuid();
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
      SibTr.exit(tc, "getDMEUuid", uuid);
    
    return uuid;
  }

}
