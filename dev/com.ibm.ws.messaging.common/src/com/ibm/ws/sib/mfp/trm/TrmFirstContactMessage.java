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
 * 164548          030502 susana   Original
 * 172028          030718 susana   Add ME messages
 * 174700          030909 susana   Reduce visibility of setMessageType
 * 179461.1        031010 vaughton Add new request/reply bridge fcm
 * 183236          031118 vaughton Add ClientAttachRequest2
 * 172521.1        040105 baldwint Schema propagation
 * 192295          040226 vaughton Add Me Bridge Bootstrap Request/Reply
 * 172521.5        040322 baldwint Remove deprecated encode methods
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.mfp.MessageEncodeFailedException;

/**
 * TrmFirstContactMessage is the basic interface for accessing and processing any
 * Topology and Routing Manager First Contact Messages.
 * <p>
 * All of the TRM First Contact messages are specializations of
 * TRMFirstContactMessage and can be 'made' from an existing TRMFirstContactMessage
 * of the appropriate type.
 * The TRMFirstContactMessage interface provides get/set methods for the common
 * fields. It also provides the method for encoding a message
 * for transmission.
 *
 */
public interface TrmFirstContactMessage {

  /* **************************************************************************/
  /* Methods for making more specialised messages                             */
  /* **************************************************************************/

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmClientBootstrapRequest.
   *
   *  @return A TrmClientBootstrapRequest representing the same message.
   */
  public TrmClientBootstrapRequest makeInboundTrmClientBootstrapRequest();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmClientBootstrapReply.
   *
   *  @return A TrmClientBootstrapReply representing the same message.
   */
  public TrmClientBootstrapReply makeInboundTrmClientBootstrapReply();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmClientAttachRequest.
   *
   *  @return A TrmClientAttachRequest representing the same message.
   */
  public TrmClientAttachRequest makeInboundTrmClientAttachRequest();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmClientAttachRequest2.
   *
   *  @return A TrmClientAttachRequest2 representing the same message.
   */
  public TrmClientAttachRequest2 makeInboundTrmClientAttachRequest2();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmClientAttachReply.
   *
   *  @return A TrmClientAttachReply representing the same message.
   */
  public TrmClientAttachReply makeInboundTrmClientAttachReply();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeConnectRequest.
   *
   *  @return A TrmMeConnectRequest representing the same message.
   */
  public TrmMeConnectRequest makeInboundTrmMeConnectRequest();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeConnectReply.
   *
   *  @return A TrmMeConnectReply representing the same message.
   */
  public TrmMeConnectReply makeInboundTrmMeConnectReply();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeLinkReply.
   *
   *  @return A TrmMeLinkReply representing the same message.
   */
  public TrmMeLinkRequest makeInboundTrmMeLinkRequest();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeLinkReply.
   *
   *  @return A TrmMeLinkReply representing the same message.
   */
  public TrmMeLinkReply makeInboundTrmMeLinkReply();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeBridgeReply.
   *
   *  @return A TrmMeBridgeReply representing the same message.
   */
  public TrmMeBridgeRequest makeInboundTrmMeBridgeRequest();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeBridgeReply.
   *
   *  @return A TrmMeBridgeReply representing the same message.
   */
  public TrmMeBridgeReply makeInboundTrmMeBridgeReply();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeBridgeBootstrapRequest
   *
   *  @return A TrmMeBridgeBootstrapRequest representing the same message.
   */
  public TrmMeBridgeBootstrapRequest makeInboundTrmMeBridgeBootstrapRequest();

  /**
   *  Convert the existing inbound TrmFirstContactMessage into a
   *  TrmMeBridgeBootstrapReply
   *
   *  @return A TrmMeBridgeBootstrapReply representing the same message.
   */
  public TrmMeBridgeBootstrapReply makeInboundTrmMeBridgeBootstrapReply();

  /* **************************************************************************/
  /* Methods for encoding                                                     */
  /* **************************************************************************/

  /**
   *  Encode the message into a byte array for transmission.
   *
   *  @param conn The CommsConnection over which the encoded message is to be sent.  This
   *  may be null if the message is not being encoded for transmission.
   *  @return A byte array containing the encoded message.
   *
   *  @exception MessageEncodeFailedException Thrown if the message could not be encoded
   */
  public byte[] encode(Object conn) throws MessageEncodeFailedException;

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the value of the TrmFirstContactMessageType from the  message.
   *
   *  @return The TrmFirstContactMessageType singleton which distinguishes
   *          the type of this message.
   */
  public TrmFirstContactMessageType getMessageType();
}
