/*
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
 * 164548          030506 susana   Original
 * 166493          030515 susana   Move factory creation to clinit
 * 166631          030519 susana   Add MessageEncodeFailed & MessageDecodeFailed Exceptions
 * 172028          030718 susana   Add ME FirstContact messages
 * 174699          030820 vaughton Add Trm control messages
 * 167577          030922 susana   Change Tr calls to SibTr
 * 177927          031002 baldwint Add offset parameter to inbound message creation
 * 177927.2        031007 vaughton Remove deprecated methods
 * 179461.1        031010 vaughton Add new request/reply bridge fcm
 * 183236          031118 vaughton Add ClientAttachRequest2
 * 189857          040206 susana   Add 'No FFDC code needed' comment
 * 192295          040226 vaughton Add Me Bridge Bootstrap Request/Reply
 * 195445.26       040514 susana   Change message prefix from SIFP to CWSIF
 * 240085          051018 kgoodson FFDC on class init failure
 * 442933          070601 susana   Add trace guard
 * ============================================================================
 */

package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.mfp.trm.TrmMessageFactory;

import com.ibm.ws.sib.mfp.*;
import com.ibm.ws.sib.utils.ras.SibTr;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;

/**
 * A singleton TrmMessageFactory is created at static initialization
 * and is subsequently used for the creation of all new and inbound TRM Messages
 * of any sub-type.
 */
public abstract class TrmMessageFactory {

  private final static String TRM_MESSAGE_FACTORY_CLASS = "com.ibm.ws.sib.mfp.impl.TrmMessageFactoryImpl";

  private static TraceComponent tc = SibTr.register(TrmMessageFactory.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  //Liberty COMMS change
  // Making TrmMessageFactoryImpl as singleton
  volatile private static TrmMessageFactory _instance=null;

  /**
   *  Get the singleton TrmMessageFactory which is to be used for
   *  creating TRM Message instances.
   *
   *  @return The TrmMessageFactory
   *
  */
  public static TrmMessageFactory getInstance()  {
	  if (_instance == null) {
		  synchronized(TrmMessageFactory.class) {
			  if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "createFactoryInstance");
			    try {
			      Class cls = Class.forName(TRM_MESSAGE_FACTORY_CLASS);
			      _instance = (TrmMessageFactory) cls.newInstance();
			    }
			    catch (Exception e) {
			      FFDCFilter.processException(e, "com.ibm.ws.sib.mfp.TrmMessageFactory.createFactoryInstance", "112");
			      SibTr.error(tc,"UNABLE_TO_CREATE_TRMFACTORY_CWSIF0021",e);
			    }
			    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "createFactoryInstance",_instance);
			  }
		   }

	  return _instance;
  }

  /**
   *  Create a new, empty TrmClientBootstrapRequest message
   *
   *  @return The new TrmClientBootstrapRequest.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmClientBootstrapRequest createNewTrmClientBootstrapRequest() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmClientBootstrapReply message
   *
   *  @return The new TrmClientBootstrapReply.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmClientBootstrapReply createNewTrmClientBootstrapReply() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmClientAttachRequest message
   *
   *  @return The new TrmClientAttachRequest.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmClientAttachRequest createNewTrmClientAttachRequest() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmClientAttachRequest2 message
   *
   *  @return The new TrmClientAttachRequest2.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmClientAttachRequest2 createNewTrmClientAttachRequest2() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmClientAttachReply message
   *
   *  @return The new TrmClientAttachReply.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmClientAttachReply createNewTrmClientAttachReply() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeConnectRequest message
   *
   *  @return The new TrmMeConnectRequest.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeConnectRequest createNewTrmMeConnectRequest() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeConnectReply message
   *
   *  @return The new TrmMeConnectReply.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeConnectReply createNewTrmMeConnectReply() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeLinkRequest message
   *
   *  @return The new TrmMeLinkRequest.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeLinkRequest createNewTrmMeLinkRequest() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeLinkReply message
   *
   *  @return The new TrmMeLinkReply.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeLinkReply createNewTrmMeLinkReply() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeBridgeRequest message
   *
   *  @return The new TrmMeBridgeRequest.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeBridgeRequest createNewTrmMeBridgeRequest() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeBridgeReply message
   *
   *  @return The new TrmMeBridgeReply.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeBridgeReply createNewTrmMeBridgeReply() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeBridgeBootstrapRequest message
   *
   *  @return The new TrmMeBridgeBootstrapRequest.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeBridgeBootstrapRequest createNewTrmMeBridgeBootstrapRequest() throws MessageCreateFailedException;

  /**
   *  Create a new, empty TrmMeBridgeBootstrapReply message
   *
   *  @return The new TrmMeBridgeBootstrapReply.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmMeBridgeBootstrapReply createNewTrmMeBridgeBootstrapReply() throws MessageCreateFailedException;

  /**
   *  Create a TrmFirstContactMessage to represent an inbound message.
   *
   *  @param rawMessage  The inbound byte array containging a complete message
   *  @param offset      The offset within the byte array at which the message begins
   *  @param length      The length of the message within the byte array
   *
   *  @return The new TrmFirstContactMessage
   *
   *  @exception MessageDecodeFailedException Thrown if the inbound message could not be decoded
   */
  public abstract TrmFirstContactMessage createInboundTrmFirstContactMessage(byte rawMessage[], int offset, int length)
                                                                            throws MessageDecodeFailedException;

  /**
   *  Create a TrmRouteData message
   *
   *  @return The new TrmRouteData.
   *
   *  @exception MessageCreateFailedException Thrown if such a message can not be created
   */
  public abstract TrmRouteData createTrmRouteData() throws MessageCreateFailedException;

}
