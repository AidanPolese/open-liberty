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
 * 158444          030207 susana   Original
 * 158992          030214 susana   Temporarily extend serialization for prototype
 * 165654          030508 susana   Remove extends serialization
 * xxxxxx          030512 susana   Move to sib.common
 * 195123          040323 susana   Move IntAble back to sib.mfp
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

/**
 * This is a 'marker' interface which indicates that it is possible to obtain
 * an integer value corresponding to an instance of the implementing class.
 */
public interface IntAble {

  /**
   * Returns the integer representation of the instance of a type-safe
   * enumeration which extends this interface.
   *
   * @return int      The integer representation of the Inttable.
   */
  public int toInt();

}
