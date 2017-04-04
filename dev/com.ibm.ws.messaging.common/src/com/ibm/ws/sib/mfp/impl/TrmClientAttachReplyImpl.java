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
package com.ibm.ws.sib.mfp.impl;

import java.util.*;

import com.ibm.ws.sib.mfp.*;
import com.ibm.ws.sib.mfp.trm.*;
import com.ibm.ws.sib.mfp.schema.TrmFirstContactAccess;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * TrmClientAttachReplyImpl extends the general TrmFirstContactMessageImpl
 * and is the implementation class for the TrmClientAttachReply interface.
 *
 */
public class TrmClientAttachReplyImpl extends TrmFirstContactMessageImpl implements TrmClientAttachReply  {

  private final static long serialVersionUID = 1L;

  private static TraceComponent tc = SibTr.register(TrmClientAttachReplyImpl.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  /* **************************************************************************/
  /* Constructors                                                             */
  /* **************************************************************************/

  /**
   *  Constructor for a new Jetstream message.
   *
   *  @exception MessageDecodeFailedException Thrown if such a message can not be created
   */
  TrmClientAttachReplyImpl() throws MessageDecodeFailedException {
    super();
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>");

    setMessageType(TrmFirstContactMessageType.CLIENT_ATTACH_REPLY);
    setFailureReason(new ArrayList());
  }

  /**
   *  Constructor for an inbound message.
   *  (Only to be called by TrmFirstContactMessage.makeInboundTrmClientAttachReply
   *
   *  @param inJmo The JsMsgObject representing the inbound message.
   */
  TrmClientAttachReplyImpl(JsMsgObject inJmo) {
    super(inJmo);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "<init>, inbound jmo ");
  }


  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /*
   *  Get the Return Code from the message.
   *
   *  Javadoc description supplied by TrmClientBootstrapRequest interface.
   */
  public Integer getReturnCode() {
    return (Integer)jmo.getField(TrmFirstContactAccess.BODY_CLIENTATTACHREPLY_RETURNCODE);
  }

  /*
   *  Get the failure reason from the message.
   *
   *  Javadoc description supplied by TrmClientBootstrapRequest interface.
   */
  public List getFailureReason() {
    List fr =  (List)jmo.getField(TrmFirstContactAccess.BODY_CLIENTATTACHREPLY_FAILUREREASON);
    if (fr != null) {
      return new ArrayList(fr);
    } else {
      return null;
    }
  }


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /*
   *  Set the Return Code in the message.
   *
   *  Javadoc description supplied by TrmClientBootstrapRequest interface.
   */
  public void setReturnCode(int value) {
    jmo.setIntField(TrmFirstContactAccess.BODY_CLIENTATTACHREPLY_RETURNCODE, value);
  }

  /*
   *  Set the failure reason in the message.
   *
   *  Javadoc description supplied by TrmClientBootstrapRequest interface.
   */
  public void setFailureReason(List value) {
    List fr = new ArrayList(value);
    jmo.setField(TrmFirstContactAccess.BODY_CLIENTATTACHREPLY_FAILUREREASON, fr);
  }
}
