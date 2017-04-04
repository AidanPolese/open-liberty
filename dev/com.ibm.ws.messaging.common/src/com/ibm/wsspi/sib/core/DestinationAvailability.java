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
 * SIB0137.core.1   230507 nyoung   addDestinationListener promoted to Core SPI
 * SIB0137.comms.2  250907 vaughton Comms implementation support
 * 569303           181208 dware    Fix javadoc for V7 common criteria
 * ===========================================================================
 */
package com.ibm.wsspi.sib.core;

/**
 * Type-Safe Enumeration indicating the Destination Availiability
 * <p>
 * This class has no security implications.
 */
public class DestinationAvailability
{
  /**
   * Indicates that the listener is only interested in SEND from an destination.
   *
   * The destination listener will be called if the destination is a non-mediated
   *  destination with a local QueuePoint or a mediated destination with a local Mediation Point.
   */
  public final static DestinationAvailability SEND = new DestinationAvailability("SEND", 0);

  /**
   * Indicates that the listener is only interested in RECEIVE from an destination.
   *
   * The destination must have a QueuePoint on the ME to which the connection is connected.
   */
  public final static DestinationAvailability RECEIVE = new DestinationAvailability("RECEIVE", 1);

  /**
   * Indicates that the listener is interested in both SEND and RECEIVE from an destination.
   */
  public final static DestinationAvailability SEND_AND_RECEIVE = new DestinationAvailability("SEND_AND_RECEIVE", 2);

  /** The value for this constant */
  private int value;

  /** The string for this constant */
  private transient String name = null;

  /**
   * Private constructor.
   */
   private DestinationAvailability(String name, int value)
   {
     this.name = name;
     this.value = value;
   }

  /**
   * @return a string representing the DestinationAvailability value
   */
  public final String toString()
  {
    return name;
  }

  /**
   * @return an integer value representing this DestinationAvailability
   */
  public final int toInt()
  {
    return value;
  }

  /**
   * Get the DestinationAvailability represented by the given integer value;
   *
   * @param value the integer representation of the required DestinationAvailability
   * @return the DestinationAvailability represented by the given integer value
   */
  private final static DestinationAvailability[] set = {SEND, RECEIVE, SEND_AND_RECEIVE};              //SIB0137.comms.2

  public final static DestinationAvailability getDestinationAvailability (final int value) {           //SIB0137.comms.2
    return set[value];                                                                                 //SIB0137.comms.2
  }                                                                                                    //SIB0137.comms.2

}
