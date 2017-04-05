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
 * 169263          030612 susana   Original
 * 167577          030922 susana   Change Tr calls to SibTr
 * 195123          040323 susana   Move IntAble back to sib.mfp
 * 195928          040413 baldwint Add Request and Reply types
 * 442933          070601 susana   Add trace guard
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.control;

import com.ibm.ws.sib.mfp.*;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * SubscriptionMessageType is a type-safe enumeration which indicates the type of a
 * Subscription Propagation message.
 */
public final class SubscriptionMessageType implements IntAble {

  private static TraceComponent tc = SibTr.register(SubscriptionMessageType.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);

  /** Integer value of the Subscription Message Types                                  */
  public final static int UNKNOWN_INT   = 0;
  public final static int RESET_INT     = 1;
  public final static int CREATE_INT    = 2;
  public final static int DELETE_INT    = 3;
  public final static int REQUEST_INT   = 4;
  public final static int REPLY_INT     = 5;
 
  /**  Constant denoting an indeterminate Subscription Propagation Message  */
  public  final static SubscriptionMessageType UNKNOWN   = new SubscriptionMessageType("UNKNOWN"  ,UNKNOWN_INT );

  /**  Constant denoting a Reset Message  */
  public  final static SubscriptionMessageType RESET     = new SubscriptionMessageType("RESET"    ,RESET_INT   );

  /**  Constant denoting a Create Message  */
  public  final static SubscriptionMessageType CREATE    = new SubscriptionMessageType("CREATE"   ,CREATE_INT  );

  /**  Constant denoting a Delete Message  */
  public  final static SubscriptionMessageType DELETE    = new SubscriptionMessageType("DELETE"   ,DELETE_INT  );

  /**  Constant denoting a Request Message  */
  public  final static SubscriptionMessageType REQUEST   = new SubscriptionMessageType("REQUEST"  ,REQUEST_INT );

  /**  Constant denoting a Reply Message  */
  public  final static SubscriptionMessageType REPLY     = new SubscriptionMessageType("REPLY"    ,REPLY_INT   );

  /*  Array of defined SubscriptionMessageTypes - needed by getSubscriptionMessageType  */
  private final static SubscriptionMessageType[] set = {UNKNOWN
                                                       ,RESET
                                                       ,CREATE
                                                       ,DELETE
                                                       ,REQUEST
                                                       ,REPLY
                                                       };

  private String name;
  private int    value;

  /* Private constructor - ensures the 'constants' defined here are the total set. */
  private SubscriptionMessageType(String aName, int aValue) {
    name  = aName;
    value = aValue;
  }

  /**
   * Returns the corresponding SubscriptionMessageType for a given integer.
   * This method should NOT be called by any code outside the MFP component.
   * It is only public so that it can be accessed by sub-packages.
   *
   * @param  aValue         The integer for which an SubscriptionMessageType is required.
   *
   * @return The corresponding SubscriptionMessageType
   */
  public final static SubscriptionMessageType getSubscriptionMessageType(int aValue) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc,"Value = " + aValue);
    return set[aValue];
  }

  /**
   * Returns the integer representation of the SubscriptionMessageType.
   * This method should NOT be called by any code outside the MFP component.
   * It is only public so that it can be accessed by sub-packages.
   *
   * @return  The int representation of the instance.
   */
  public final int toInt() {
    return value;
  }

  /**
   * Returns the name of the SubscriptionMessageType.
   *
   * @return  The name of the instance.
   */
  public final String toString() {
    return name;
  }

}
