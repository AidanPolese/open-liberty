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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- ------------------------------------------
 * 181718.1         031216 vaughton Original
 * ===========================================================================
 */

package com.ibm.ws.sib.utils;

/**
 * This class represents a 12 byte long UUID
 */

public final class SIBUuid12 extends SIBUuidLength {

  private static final int LENGTH = 12;
  private static final String zeroString = new SIBUuid12(new byte[LENGTH]).toString();

  /**
   * Construct a SIBUuid12 object.
   */

  public SIBUuid12 () {
    super(LENGTH);
  }

  /**
   * Construct a SIBUuid12 object from a byte array. The byte array length is
   * not significant, if less than 12 bytes long then 0 padding will be applied,
   * if longer than 12 bytes then the first 12 bytes of the byte array will be
   * used.
   *
   * @param bytes The byte array representing the UUID
   */

  public SIBUuid12 (byte[] bytes) {
    super(LENGTH, bytes);
  }

  /**
   * Construct a SIBUuid12 object from a String. The string length is not
   * significant, if less than 12 characters then 0 padding will be applied,
   * if longer than 12 characters then the first 12 characters of the string will
   * be used. Only hexadecimal and dash characters are permitted in the string.
   *
   * @param string The string representing the UUID
   */

  public SIBUuid12 (String string) {
    super(LENGTH, string);
  }

  /**
   * Return the zero SIBUuid12 representation
   *
   * @return A string representing the zero SIBUuid12
   */

  public static String toZeroString () {
    return zeroString;
  }

}
