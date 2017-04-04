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
 * ---------------  ------ -------- ------------------------------------------
 * 246197           031204 gatfora  Original
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.corespitrace;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.TraceGroups;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author gatfora
 * 
 * Trace class to provide filtering of all Core SPI calls 
 * made against the SIConnectionFactory interface.
 * 
 * Filtering can be done against 
 * com.ibm.ws.sib.processor.impl.trace.CoreSPIConnFactory
 *
 */
public class CoreSPIConnFactory
{

  //trace for messages
  public static final TraceComponent tc =
    SibTr.register(
      CoreSPIConnFactory.class,
      TraceGroups.TRGRP_PROCESSOR,
      SIMPConstants.TRACE_MESSAGE_RESOURCE_BUNDLE);

  // NLS for component
  public static final TraceNLS nls =
    TraceNLS.getTraceNLS(SIMPConstants.TRACE_MESSAGE_RESOURCE_BUNDLE);
}
