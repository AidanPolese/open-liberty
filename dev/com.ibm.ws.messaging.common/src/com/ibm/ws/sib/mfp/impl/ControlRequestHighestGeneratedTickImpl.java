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
 *                                 Version 1.12 copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.mfp.MessageDecodeFailedException;
import com.ibm.ws.sib.mfp.MfpConstants;
import com.ibm.ws.sib.mfp.control.ControlRequestHighestGeneratedTick;
import com.ibm.ws.sib.mfp.control.ControlMessageType;
import com.ibm.ws.sib.mfp.schema.ControlAccess;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 *  ControlRequestHighestGeneratedTickImpl extends ControlMessageImpl and hence JsMessageImpl,
 *  and is the implementation class for the ControlRequestHighestGeneratedTick interface.
 */
public class ControlRequestHighestGeneratedTickImpl extends ControlMessageImpl implements ControlRequestHighestGeneratedTick {

  private final static long serialVersionUID = 1L;

  private final static TraceComponent tc = SibTr.register(ControlRequestHighestGeneratedTickImpl.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);


  /**
   *  Constructor for a new Control Request Highest Generated Tick Message.
   *
   *  This constructor should never be used except by JsMessageImpl.createNew().
   *  The method must not actually do anything.
   */
  public ControlRequestHighestGeneratedTickImpl() {
  }

  /**
   *  Constructor for a new Control Request Highest Generated Tick Message.
   *  To be called only by the ControlMessageFactory.
   *
   *  @param flag No-op flag to distinguish different constructors.
   *
   *  @exception MessageDecodeFailedException Thrown if such a message can not be created
   */
  public ControlRequestHighestGeneratedTickImpl(int flag) throws MessageDecodeFailedException {
    super(flag);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>");
    setControlMessageType(ControlMessageType.REQUESTHIGHESTGENERATEDTICK);
  }

  /**
   *  Constructor for an inbound message.
   *  (Only to be called by a superclass make method.)
   *
   *  @param inJmo The JsMsgObject representing the inbound message
   */
  public ControlRequestHighestGeneratedTickImpl(JsMsgObject inJmo) {
    super(inJmo);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>, inbound jmo ");
  }

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlHighestGeneratedTick#getRequestID()
   */
  public final long getRequestID() {
    return jmo.getLongField(ControlAccess.BODY_REQUESTHIGHESTGENERATEDTICK_REQUESTID);
  }

  /*
   * Get summary trace line for this message 
   * 
   *  Javadoc description supplied by ControlMessage interface.
   */
  public void getTraceSummaryLine(StringBuilder buff) {
    
    // Get the common fields for control messages
    super.getTraceSummaryLine(buff);
    
    buff.append("requestID=");
    buff.append(getRequestID());
    
  }
  
  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlHighestGeneratedTick#setRequestID(long)
   */
  public final void setRequestID(long value) {
    jmo.setLongField(ControlAccess.BODY_REQUESTHIGHESTGENERATEDTICK_REQUESTID, value);
  }

}
