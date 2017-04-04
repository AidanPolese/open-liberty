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
 * 209436.0        150604 jroots   Original
 * 276259          130505 dware    Improve security related javadoc
 * SIB0136.core.1  011106 nyoung   Add new XPath1.0 SelectorDomain
 * SIB0136.core.2  031106 nyoung   Add new XPath SelectorDomain to supported set.
 * 499859          080222 djvines  Implement hashCode
 * ============================================================================
 */
package com.ibm.wsspi.sib.core;

/**
 SelectorDomain is a "Java typesafe enum", the values of which represent
 different the different messaging domains in which a message selector can be
 defined. A SelectorDomain parameter is used in the
 SelectionCriteriaFactory.createMessageSelector method to convey messaging
 context.

 The Selector Domains that are supported are the SIMessage domain and the JMS
 domain.
  <p>
  This class has no security implications.
*/

public class SelectorDomain
{
  /**
   A SelectorDomain of type SIMessage is indicated by the value
   SelectorDomain.SIMESSAGE
  */
  public final static SelectorDomain SIMESSAGE
    = new SelectorDomain ("SIMessage",0);

  /**
   A SelectorDomain of type JMS is indicated by the value
   SelectorDomain.JMS
  */
  public final static SelectorDomain JMS
    = new SelectorDomain ("JMS",1);

  /**
   A SelectorDomain of type XPath1.0 is indicated by the value
   SelectorDomain.XPATH1
  */
  public final static SelectorDomain XPATH1
    = new SelectorDomain ("XPATH1",2);

  /** ALERT - If you add a new Selector Domain remember to modify the set below also
   */

  /**
   Returns a string representing the SelectorDomain value

   @return a string representing the SelectorDomain value
  */
  public final String toString() {
    return name;
  }

  /**
   * Returns an integer value representing this SelectorDomain
   *
   * @return an integer value representing this SelectorDomain
   */
  public final int toInt()
  {
    return value;
  }

  /**
   * equals method.
   */
  public boolean equals(Object other)
  {
    boolean res = false;
    if(other instanceof SelectorDomain)
    {
      SelectorDomain otherDomain = (SelectorDomain)other;
      if(otherDomain.value == value)
        res = true;
    }
    return res;
  }

  /**
   * hashCode method.
   */
  public int hashCode()
  {
    return value;
  }

  /**
   * Get the SelectorDomain represented by the given integer value;
   *
   * @param value the integer representation of the required SelectorDomain
   * @return the SelectorDomain represented by the given integer value
   */
  public final static SelectorDomain getSelectorDomain (int value)
  {
    return set[value];
  }

  private final String name;
  private final int value;
  private final static SelectorDomain [] set = {SIMESSAGE, JMS, XPATH1};
  private SelectorDomain (String name, int value) {
    this.name = name;
    this.value = value;
  }
}

