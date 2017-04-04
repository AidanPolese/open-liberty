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
 * 178364          031106 gatfora  Original
 * 178364.2        031231 susana   Tidy up code
 * 186248.1.6      040520 susana   Remove EnvelopeType part 2
 * 215177          040423 susana   Change Control Messages to single part messages
 * LIDB3706-5.228  050125 susana   Add serialVersionUID
 * 442933          070531 susana   Add trace guard
 * 599149          090701 pbroad   Add minimal ME<->ME comms trace 
 * 605141          090825 pbroad   Use StringBuilder instead of StringBuffer for trace summary line
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.mfp.MessageDecodeFailedException;
import com.ibm.ws.sib.mfp.MfpConstants;
import com.ibm.ws.sib.mfp.control.ControlAreYouFlushed;
import com.ibm.ws.sib.mfp.control.ControlMessageType;
import com.ibm.ws.sib.mfp.schema.ControlAccess;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 *  ControlAreYouFlushedImpl extends ControlMessageImpl and hence JsMessageImpl,
 *  and is the implementation class for the ControlAreYouFlushed interface.
 */
public class ControlAreYouFlushedImpl extends ControlMessageImpl implements ControlAreYouFlushed {

  private final static long serialVersionUID = 1L;

  private final static TraceComponent tc = SibTr.register(ControlAreYouFlushedImpl.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  /**
   *  Constructor for a new Control Are You Flushed Get Message.
   *
   *  This constructor should never be used except by JsMessageImpl.createNew().
   *  The method must not actually do anything.
   */
  public ControlAreYouFlushedImpl() {
  }

  /**
   *  Constructor for a new Control Browse Get Message.
   *  To be called only by the ControlMessageFactory.
   *
   *  @param flag No-op flag to distinguish different constructors.
   *
   *  @exception MessageDecodeFailedException Thrown if such a message can not be created
   */
  public ControlAreYouFlushedImpl(int flag) throws MessageDecodeFailedException {
    super(flag);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>");
    setControlMessageType(ControlMessageType.AREYOUFLUSHED);
  }

  /**
   *  Constructor for an inbound message.
   *  (Only to be called by a superclass make method.)
   *
   *  @param inJmo The JsMsgObject representing the inbound message
   */
  ControlAreYouFlushedImpl(JsMsgObject inJmo) {
    super(inJmo);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>, inbound jmo ");
  }

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlAreYouFlushed#getRequestID()
   */
  public final long getRequestID() {
    return jmo.getLongField(ControlAccess.BODY_AREYOUFLUSHED_REQUESTID);
  }

  /*
   * Get summary trace line for this message 
   * 
   *  Javadoc description supplied by ControlMessage interface.
   */
  public void getTraceSummaryLine(StringBuilder buff) {
    
    // Get the common fields for control messages
    super.getTraceSummaryLine(buff);
    
    buff.append(",requestID=");
    buff.append(getRequestID());
    
  }

  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /* (non-Javadoc)
   * @see com.ibm.ws.sib.mfp.control.ControlAreYouFlushed#setRequestID(long)
   */
  public final void setRequestID(long value) {
    jmo.setLongField(ControlAccess.BODY_AREYOUFLUSHED_REQUESTID, value);
  }

}
