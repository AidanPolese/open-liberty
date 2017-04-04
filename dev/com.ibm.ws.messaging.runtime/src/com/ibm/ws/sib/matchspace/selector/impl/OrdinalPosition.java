/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5639-D57,5630-A36,5630-A37,Copyright IBM Corp. 2012
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
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

public class OrdinalPosition 
{

  /** Specifies level at which this ordinal position applies, always 0 for JMS identifiers */
  public int majorPosition = 0;
  
  /** Specifies level at which this ordinal position applies within a level or
   * step as defined by the majorPosition */
  private int minorPosition = 0;

  /** Create a new SimpleMatcher for a given Identifier */

  public OrdinalPosition(int major, int minor)
  {
    majorPosition = major;
    minorPosition = minor;
  }

  public int compareTo(Object o)
  {
    int ret = 0;
    // Class cast exc
    OrdinalPosition other = (OrdinalPosition)o;
    
    if(other.majorPosition == majorPosition)
    {
      ret = minorPosition - other.minorPosition;
    }
    else
      ret = majorPosition - other.majorPosition;
     
    return ret;
  }  

  public boolean equals(Object o)
  {
    if (o instanceof OrdinalPosition)
    {
      if(((OrdinalPosition)o).majorPosition == majorPosition)
      {
        // Majors are equal, compare minors
        if(((OrdinalPosition)o).minorPosition == minorPosition)
          return true; // minors are equal
        else
          return false; // minors are unequal
      }
      else
        return false; // majors are unequal 
    }
    else
      return false; // different types of object
  }    
  
  public String toString()
  {
    return "Major: " + majorPosition + " minor: " + minorPosition;
  }  
}
