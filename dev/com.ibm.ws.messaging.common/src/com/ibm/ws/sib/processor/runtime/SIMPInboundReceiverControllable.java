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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.19        020704 tevans   PtoP Inbound Receiver Control
 * 186484.19.2      050704 tevans   Inbound Receiver Control
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.exceptions.SIMPRuntimeOperationFailedException;

/**
 *
 */
public interface SIMPInboundReceiverControllable extends SIMPDeliveryStreamSetReceiverControllable
{
  /**
   * If the target has been restored from a backup and does not want to risk 
   *  reprocessing a retransmitted message. There is a possibility that the request 
   *  will not reach the source quickly but we will not make any new requests on this 
   *  stream set. Invoking this causes the source to 
   *  execute clearMessagesAtSource(IindoubtAction).
   *  This is performed on all streams in the stream set.
   *  The request to do this is hardened and will complete after a restart if
   *  necessary.
   */
  public void requestFlushAtSource(boolean indoubtDiscard)
    throws SIMPRuntimeOperationFailedException, SIMPControllableNotFoundException;
}
