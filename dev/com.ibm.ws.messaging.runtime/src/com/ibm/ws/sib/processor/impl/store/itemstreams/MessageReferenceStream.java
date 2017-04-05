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
package com.ibm.ws.sib.processor.impl.store.itemstreams;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.interfaces.SIMPMessage;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author tevans
 */
public abstract class MessageReferenceStream extends SIMPReferenceStream
{
  /**
   * Trace.
   */
  private static TraceComponent tc =
    SibTr.register(
      MessageReferenceStream.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
   
   
  /**
   * Warm start constructor invoked by the Message Store.
   */
  public MessageReferenceStream()
  {
    super();

    // This space intentionally blank
  }

  /**
   * Method registerListeners.
   * <p>Register any message event listeners</p>
   * @param msg
   */
  public abstract void registerListeners(SIMPMessage msg);

}
