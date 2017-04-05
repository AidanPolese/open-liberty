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
 * --------------  ------ -------- -------------------------------------------------
 * 175492          030912 baldwint Original
 * 167577          030922 susana   Change Tr calls to SibTr
 * 181801.5        031217 vaughton Switch to new constants file
 * 187000          040312 astley   Support for remote durable pub/sub
 * 193585.5        040520 susana   Move to MFP so implement Intable etc
 * 186248          040812 susana   Store Enums as bytes
 * 442933          070601 susana   Add trace guard
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp;

import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;

public class ProtocolType {
  private static TraceComponent tc = SibTr.register(ProtocolType.class, MfpConstants.MSG_GROUP, MfpConstants.MSG_BUNDLE);
  
  /**
   * UNKNOWN type
   */
  public static final ProtocolType UNKNOWN =      new ProtocolType("UNKNOWN",        (byte)0);

  /**
   * UNICASTINPUT type
   */
  public static final ProtocolType UNICASTINPUT =  new ProtocolType("UNICASTINPUT",  (byte)1);

  /**
   * UNICASTOUTPUT type
   */
  public static final ProtocolType UNICASTOUTPUT = new ProtocolType("UNICASTOUTPUT", (byte)2);

  /**
   * PUBSUBINPUT type
   */
  public static final ProtocolType PUBSUBINPUT =   new ProtocolType("PUBSUBINPUT",   (byte)3);

  /**
   * PUBSUBOUTPUT type
   */
  public static final ProtocolType PUBSUBOUTPUT =  new ProtocolType("PUBSUBOUTPUT",  (byte)4);

    /**
   * ANYCASTNPUT type
   */
  public static final ProtocolType ANYCASTINPUT =  new ProtocolType("ANYCASTINPUT",  (byte)5);

  /**
   * ANYCASTOUTPUT type
   */
  public static final ProtocolType ANYCASTOUTPUT = new ProtocolType("ANYCASTOUTPUT", (byte)6);

  /**
   * DURABLEINPUT type
   */
  public static final ProtocolType DURABLEINPUT =  new ProtocolType("DURABLEINPUT",  (byte)7);

  /**
   * DURABLEOUTPUT type
   */
  public static final ProtocolType DURABLEOUTPUT = new ProtocolType("DURABLEOUTPUT", (byte)8);

  private final static ProtocolType[] set = {
    UNKNOWN,
    UNICASTINPUT,
    UNICASTOUTPUT,
    PUBSUBINPUT,
    PUBSUBOUTPUT,
    ANYCASTINPUT,
    ANYCASTOUTPUT,
    DURABLEINPUT,
    DURABLEOUTPUT
  };

  private final String name;
  private final Byte   value;

  /**
   * Return the name of the ProtocolType type as a string
   * @return String name of the DestinationType type
   */
  public final String toString () {
    return name;
  }

  /**
   * Returns the Byte representation of the ProtocolType.
   * This method should NOT be called by any code outside the MFP component.
   * It is only public so that it can be accessed by sub-packages.
   *
   * @return The Byte representation of the instance.
   */
  public final Byte toByte() {
    return value;
  }

  /**
   * Method getProtocolType.
   * @param aValue
   * @return ProtocolType
   */
  public final static ProtocolType getProtocolType(Byte aValue) {
    return set[aValue.intValue()];
  }

  /*
   * Constructor ProtocolType.
   */
  // Private constructor prevents this class being extended so there is no need
  // to make this class final
  private ProtocolType (String name, byte aValue) {
    this.name = name;
    this.value = new Byte(aValue);
  }
}
