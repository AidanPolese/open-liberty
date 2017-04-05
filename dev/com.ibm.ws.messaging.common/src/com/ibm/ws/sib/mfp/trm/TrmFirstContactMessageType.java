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
 * 165989          030513 susana   Move IntAble to sib.common
 * 172028          030718 susana   Add ME messages
 * 167577          030922 susana   Change Tr calls to SibTr
 * 179461.1        031010 vaughton Add new request/reply bridge fcm's
 * 182236          031118 vaughton Add ClientAttachRequest2
 * 192295          040226 vaughton Add Me Bridge Bootstrap Request/Reply
 * 195123          040323 susana   Move IntAble back to sib.mfp
 * 442933          070601 susana   Add trace guard
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

import com.ibm.ws.sib.mfp.*;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * TrmFirstContactMessageType is a type-safe enumeration which indicates the
 * type of a TRM First Contact message.
 */
public final class TrmFirstContactMessageType implements IntAble {

  private static TraceComponent tc = SibTr.register(TrmFirstContactMessageType.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  /**  Constant denoting a Client Bootstrap Request  */
  public  final static TrmFirstContactMessageType CLIENT_BOOTSTRAP_REQUEST    = new TrmFirstContactMessageType("CLIENT_BOOTSTRAP_REQUEST"    ,0);

  /**  Constant denoting a Client Bootstrap Reply  */
  public  final static TrmFirstContactMessageType CLIENT_BOOTSTRAP_REPLY      = new TrmFirstContactMessageType("CLIENT_BOOTSTRAP_REPLY"      ,1);

  /**  Constant denoting a Client Attach Request  */
  public  final static TrmFirstContactMessageType CLIENT_ATTACH_REQUEST       = new TrmFirstContactMessageType("CLIENT_ATTACH_REQUEST"       ,2);

  /**  Constant denoting a Client Attach Request 2 */
  public  final static TrmFirstContactMessageType CLIENT_ATTACH_REQUEST2      = new TrmFirstContactMessageType("CLIENT_ATTACH_REQUEST2"      ,3);

  /**  Constant denoting a Client Attach Reply  */
  public  final static TrmFirstContactMessageType CLIENT_ATTACH_REPLY         = new TrmFirstContactMessageType("CLIENT_ATTACH_REPLY"         ,4);

  /**  Constant denoting a ME Bootstrap Request  */
  public  final static TrmFirstContactMessageType ME_CONNECT_REQUEST          = new TrmFirstContactMessageType("ME_CONNECT_REQUEST"          ,5);

  /**  Constant denoting a ME Bootstrap Reply  */
  public  final static TrmFirstContactMessageType ME_CONNECT_REPLY            = new TrmFirstContactMessageType("ME_CONNECT_REPLY"            ,6);

  /**  Constant denoting a ME Link Request  */
  public  final static TrmFirstContactMessageType ME_LINK_REQUEST             = new TrmFirstContactMessageType("ME_LINK_REQUEST"             ,7);

  /**  Constant denoting a ME Link Reply  */
  public  final static TrmFirstContactMessageType ME_LINK_REPLY               = new TrmFirstContactMessageType("ME_LINK_REPLY"               ,8);

  /**  Constant denoting a ME Bridge Request  */
  public  final static TrmFirstContactMessageType ME_BRIDGE_REQUEST           = new TrmFirstContactMessageType("ME_BRIDGE_REQUEST"           ,9);

  /**  Constant denoting a ME Bridge Reply  */
  public  final static TrmFirstContactMessageType ME_BRIDGE_REPLY             = new TrmFirstContactMessageType("ME_BRIDGE_REPLY"             ,10);

  /**  Constant denoting a ME Bridge Bootstrap Request */
  public  final static TrmFirstContactMessageType ME_BRIDGE_BOOTSTRAP_REQUEST = new TrmFirstContactMessageType("ME_BRIDGE_BOOTSTRAP_REQUEST", 11);

  /**  Constant denoting a ME Bridge Bootstrap Reply */
  public  final static TrmFirstContactMessageType ME_BRIDGE_BOOTSTRAP_REPLY   = new TrmFirstContactMessageType("ME_BRIDGE_BOOTSTRAP_REPLY",   12);

  /*  Array of defined TrmFirstContactMessageTypes - needed by getTrmFirstContactMessageType  */
  private final static TrmFirstContactMessageType[] set = {CLIENT_BOOTSTRAP_REQUEST
                                                          ,CLIENT_BOOTSTRAP_REPLY
                                                          ,CLIENT_ATTACH_REQUEST
                                                          ,CLIENT_ATTACH_REQUEST2
                                                          ,CLIENT_ATTACH_REPLY
                                                          ,ME_CONNECT_REQUEST
                                                          ,ME_CONNECT_REPLY
                                                          ,ME_LINK_REQUEST
                                                          ,ME_LINK_REPLY
                                                          ,ME_BRIDGE_REQUEST
                                                          ,ME_BRIDGE_REPLY
                                                          ,ME_BRIDGE_BOOTSTRAP_REQUEST
                                                          ,ME_BRIDGE_BOOTSTRAP_REPLY
                                                          };

  private String name;
  private int    value;

  /* Private constructor - ensures the 'constants' defined here are the total set. */
  private TrmFirstContactMessageType(String aName, int aValue) {
    name  = aName;
    value = aValue;
  }

  /**
   * Returns the corresponding TrmFirstContactMessageType for a given integer.
   * This method should NOT be called by any code outside the MFP component.
   * It is only public so that it can be accessed by sub-packages.
   *
   * @param  aValue         The integer for which an TrmFirstContactMessageType is required.
   *
   * @return The corresponding TrmFirstContactMessageType
   */
  public final static TrmFirstContactMessageType getTrmFirstContactMessageType(int aValue) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc,"Value = " + aValue);
    return set[aValue];
  }

  /**
   * Returns the integer representation of the TrmFirstContactMessageType.
   * This method should NOT be called by any code outside the MFP component.
   * It is only public so that it can be accessed by sub-packages.
   *
   * @return  The int representation of the instance.
   */
  public final int toInt() {
    return value;
  }

  /**
   * Returns the name of the TrmFirstContactMessageType.
   *
   * @return  The name of the instance.
   */
  public final String toString() {
    return name;
  }

}
