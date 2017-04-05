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
 * 569303           181208 dware    Fix javadoc for V7 common criteria
 * ===========================================================================
 */
package com.ibm.wsspi.sib.core;

import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.wsspi.sib.core.SICoreConnection;

/**
 * The Destination Listener interface
 * <p>
 * The Destination listener is called every time destination becomes available after the listener
 *  is registered.
 * <p>
 * This class has no security implications. Security checks are performed at the time that a DestinationListener
 * is registered using SICoreConnection.addDestinationListener()
 */
public interface DestinationListener
{
  /**
   * This method is called when a destination becomes available after the listener is registered.
   * 
   * A destination 'matches' a DestinationListener registration if ALL of the following conditions are met.
   *   - 1) The destination is not a TopicSpace, and has a DestinationType matching the registration.
   *   - 2) If the user specifies destinationAvailability=RECEIVE, then the destination must have a QueuePoint 
   *        on the ME to which the connection is connected; if the user specifies destinationAvailabilty=SEND 
   *        then the destiation must be a non-mediated destination with a local QueuePoint or a mediated 
   *        destination with a local Mediation Point.
   *   - 3) Either (a) the registration specified a DestinationAvailability of SEND or SEND_AND_RECEIVE and 
   *        the destination and localization are available for send; or (b) the registration specified a 
   *        DestinationAvailability of SEND or SEND_AND_RECEIVE and the destination and localization are 
   *        available for receive. 'Available' is interpreted in a manner consistent with the createProducerSession
   *        and createConsumerSession methods. For example
   *          a) The destination must have SendAllowed or ReceiveAllowed set to true.
   *          b) The localization must have SendAllower or ReceiveAllowed set to true.
   *          c) The Subject associated with the connection must have permission to send to/receive from the destination.
   *  
   * @param connection  the connection that this destination is using
   * @param destinationAddress  the address of the destination that is available
   * @param destinationAvailability  the destinationAvailability of that destination
   */
  void destinationAvailable(SICoreConnection connection, 
                            SIDestinationAddress destinationAddress, 
                            DestinationAvailability  destinationAvailability);
}
