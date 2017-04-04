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
 *                                 Version 1.5 copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.impl;

import com.ibm.ws.sib.mfp.impl.JsMessageHandleFactory;
import com.ibm.ws.sib.mfp.JsMessageHandle;
import com.ibm.ws.sib.mfp.MfpConstants;

import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.ws.sib.utils.ras.SibTr;

import com.ibm.websphere.ras.TraceComponent;

/**
 *  This class extends the abstract com.ibm.ws.sib.mfp.JsMessageHandleFactory
 *  class and provides the concrete implementations of the methods for
 *  creating SIMessageHandles and JsMessageHandles.
 *  <p>
 *  The class must be public so that the abstract class static
 *  initialization can create an instance of it at runtime.
 *
 */
public final class JsMessageHandleFactoryImpl extends JsMessageHandleFactory {

  private static TraceComponent tc = SibTr.register(JsMessageHandleFactoryImpl.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  /**
   *  Create a new JsMessageHandle to represent an SIBusMessage.
   *
   *  @param destinationName  The name of the SIBus Destination
   *  @param localOnly        Indicates that the Destination should be localized
   *                          to the local Messaging Engine.
   *
   *  @return JsMessageHandle The new JsMessageHandle.
   *
   *  @exception NullPointerException Thrown if either parameter is null.
   */
  public final JsMessageHandle createJsMessageHandle(SIBUuid8 uuid
                                                    ,long     value
                                                    )
                                                    throws NullPointerException {
    if (uuid == null)  {
      throw new NullPointerException("uuid");
    }
    return new JsMessageHandleImpl(uuid, Long.valueOf(value));

  }

}
