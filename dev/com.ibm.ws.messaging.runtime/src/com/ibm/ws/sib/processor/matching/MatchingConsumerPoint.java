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
 * SIB0002.mp.1     210605 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.mp.1     210605 tevans   PEV Prototype
 * 309940           031005 gatfora  Missing trace statements
 * 423941           260407 vaughton Findbugs problem
 * SIB0113a.mp.1    240707 cwilkin  Gathered Consumer foundation
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.matching;

import com.ibm.ws.sib.processor.impl.interfaces.DispatchableKey;

/**
 * @author Neil Young
 *
 * <p>The MessageProcessorMatchTarget class is a wrapper that holds a ConsumerPoint,
 * but allows a MatchTarget type to be associated with it for storage in the
 * MatchSpace.

 */
public class MatchingConsumerPoint extends MessageProcessorMatchTarget{

  private DispatchableKey consumerPointData;

  MatchingConsumerPoint(DispatchableKey cp)
  {
    super(JS_CONSUMER_TYPE);
    consumerPointData = cp;
  }

  public boolean equals(Object o)
  {
    boolean areEqual = false;
    if (o instanceof MatchingConsumerPoint)
    {
      DispatchableKey otherCP = ((MatchingConsumerPoint) o).consumerPointData;

      if(consumerPointData.equals(otherCP))
        areEqual = true;
    }
    return areEqual;
  }

  public int hashCode()
  {
    return consumerPointData.hashCode();
  }
  /**
   * Returns the consumerPoint.
   * @return ConsumerPoint
   */
  public DispatchableKey getConsumerPointData()
  {
    return consumerPointData;
  }

}
