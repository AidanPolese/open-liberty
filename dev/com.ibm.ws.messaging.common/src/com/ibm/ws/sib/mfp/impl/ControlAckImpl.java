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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 171889          030722 susana   Original
 * 173701          030915 susana   Constructor changes for createNew improvements
 * 167577          030922 susana   Change Tr calls to SibTr
 * 176233          031105 baldwint Future schema compatibility problems
 * 178364.2        031231 susana   Tidy up code
 * 186248.1.6      040520 susana   Remove EnvelopeType part 2
 * 215177          040423 susana   Change Control Messages to single part messages
 * 442933          070531 susana   Add trace guard
 * 599149          090701 pbroad   Add minimal ME<->ME comms trace 
 * 605141          090825 pbroad   Use StringBuilder instead of StringBuffer for trace summary line
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.impl;

import com.ibm.ws.sib.mfp.*;
import com.ibm.ws.sib.mfp.control.*;
import com.ibm.ws.sib.mfp.schema.ControlAccess;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *  ControlAck extends ControlMessageImpl and hence JsMessageImpl,
 *  and is the implementation class for the ControlAck interface.
 */
final class ControlAckImpl extends ControlMessageImpl implements ControlAck {

  private final static long serialVersionUID = 1L;

  private static TraceComponent tc = SibTr.register(ControlAckImpl.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);


  /* **************************************************************************/
  /* Constructors                                                             */
  /* **************************************************************************/

  /**
   *  Constructor for a new Control Ack Message.
   *
   *  This constructor should never be used except by JsMessageImpl.createNew().
   *  The method must not actually do anything.
   */
  ControlAckImpl() {
  }

  /**
   *  Constructor for a new Control Ack Message.
   *  To be called only by the ControlMessageFactory.
   *
   *  @param flag No-op flag to distinguish different constructors.
   *
   *  @exception MessageDecodeFailedException Thrown if such a message can not be created
   */
  ControlAckImpl(int flag)  throws MessageDecodeFailedException {
    super(flag);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>");
    setControlMessageType(ControlMessageType.ACK);
  }


  /**
   *  Constructor for an inbound message.
   *  (Only to be called by a superclass make method.)
   *
   *  @param inJmo The JsMsgObject representing the inbound message
   */
  ControlAckImpl(JsMsgObject inJmo) {
    super(inJmo);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>, inbound jmo ");
  }


  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /*
   *  Get the Ack Prefix from the message.
   *
   *  Javadoc description supplied by ControlAck interface.
   */
  public final long  getAckPrefix() {
    return jmo.getLongField(ControlAccess.BODY_ACK_ACKPREFIX);
  }

  /*
   * Get summary trace line for this message 
   * 
   *  Javadoc description supplied by ControlMessage interface.
   */
  public void getTraceSummaryLine(StringBuilder buff) {
    
    // Get the common fields for control messages
    super.getTraceSummaryLine(buff);
    
    buff.append(",ackPrefix=");
    buff.append(getAckPrefix());
    
  }

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /*
   *  Set the Ack Prefix in the message.
   *
   *  Javadoc description supplied by ControlAck interface.
   */
  public final void setAckPrefix(long value) {
    jmo.setLongField(ControlAccess.BODY_ACK_ACKPREFIX, value);
  }
}
