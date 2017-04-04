/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5639-D57, 5630-A36, 5630-A37, Copyright IBM Corp. 2012
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
 * 171905.6         110803 tevans   Remote flows
 * SIB0113b.mp.1    040907 dware    Initial support for SIB0113b function (moved)
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.ws.sib.mfp.JsDestinationAddress;

public interface MessageProducer
{	
  public boolean isRoutingDestinationSet();

  public boolean fixedMessagePoint();

  public JsDestinationAddress getRoutingDestination();

  public void setRoutingAddress(JsDestinationAddress routingAddr);
}
