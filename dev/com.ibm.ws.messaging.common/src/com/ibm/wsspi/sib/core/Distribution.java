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
 * ---------------  ------ -------- -------------------------------------------------
 * 163029           090403 cwilkin  Original
 * 162915           140403 tevans   Added int value
 * 166828.2         060603 millwood Move to SIB.common from SIB.processor
 * 195758.0         050404 jroots   Milestone 7.5 Core SPI changes
 * 276259           130505 dware    Improve security related javadoc
 * ===========================================================================
 */

package com.ibm.wsspi.sib.core;

/**
 Distribution relates to the distribution model that a Destination possesses
 or a Session requires.
 <p>
 This class has no security implications.
 */
public class Distribution {
  
  /**
   * ONE type
   */
  public static final Distribution ONE  = new Distribution("ONE",0);
  
  /**
   * ALL type
   */
  public static final Distribution ALL  = new Distribution("ALL",1);
  
  private static final Distribution[] set = {ONE
                                              ,ALL
                                              };


  private final String name;
  private final int value;

  /**
   * Return the name of the Distribution type as a string
   *
   * @return String name of the Distribution type
   */

  public final String toString () {
    return name;
  }
  
  /**
   * Method toInt.
   * @return int
   */
  public final int toInt () {
    return value;
  }
  
  /**
   * Method getDistribution.
   * @param aValue
   * @return Distribution
   */
  public final static Distribution getDistribution(int aValue) {
    return set[aValue];
  }

  /**
   * Method Distribution.
   * @param name
   * @param value
   */
  // Private constructor prevents this class being extended so there is no need
  // to make this class final

  private Distribution (String name, int value) {
    this.name = name;
    this.value = value;
  }


}
