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
 * 174319          140803 millwood Make destination operations atomic
 * 174624.2        260803 gatfora  Refactoring public/private/protected methods
 * 176658.3.3      290104 jroots   Refactoring for destination mediation support  
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl;

/**
 * SIMPState is a "Java typesafe enum", the values of which represent 
 * different states that a message processor object can be in, for example       
 * locked, deleted etc                                                        
 */
final class SIMPState
{
  public final static SIMPState READY_FOR_USE = new SIMPState("ReadyForUse", 0);
  public final static SIMPState LOCKED = new SIMPState("Locked", 1);
  public final static SIMPState DELETED = new SIMPState("Deleted", 2);

  /**
   * @see java.lang.Object#toString()
   */
  public final String toString()
  {
    return name;
  }

  /**
   * Method toInt.
   * @return int
   */
  public final int toInt()
  {
    return value;
  }

  /**
   * Get the SIMPState represented by the given integer value;
   * 
   * @param value the integer representation of the required DestinationType
   * @return the DestinationType represented by the given integer value
   */
  public final static SIMPState getState(int value)
  {
    return set[value];
  }

  private final String name;
  private final int value;
  private final static SIMPState[] set =
    {
      READY_FOR_USE,
      LOCKED,
      DELETED };

  private SIMPState(String name, int value)
  {
    this.name = name;
    this.value = value;
  }
}
