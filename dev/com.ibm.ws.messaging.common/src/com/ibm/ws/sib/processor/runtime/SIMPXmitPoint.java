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
 * 186484.2         150304 tevans   Some intial controllable interfaces
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * 186484.10        170504 tevans   MBean Registration
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements#
 * 186484.12        110604 ajw      Finish off runtime controllable interfaces
 * 195809.4         170604 prmf     Queue Depth Limits - new attributes
 * 186484.16        220604 tevans   Xmit Queue runtime control
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 224010           130804 gatfora  Remove getMaxMsgs and setMaxMsgs.
 * 233063           200904 prmf     Remove receiveAllowed from localization
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * The interface presented by a queueing point localization to perform dynamic
 * control operations.
 * <p>
 * The operations in this interface are specific to a queueing point.
 */
public interface SIMPXmitPoint extends SIMPControllable
{
  /**
   * Get the parent Message handler
   * @return The Message Handler to which this Localization belongs.
   */
  public SIMPMessageHandlerControllable getMessageHandler();

  /**
   * Returns the high messages limit property.
   *
   * @return The destination high messages threshold for this localization.
   */
  public long getDestinationHighMsgs();

  /**
   * Allows the unique id of this localization to be obtained and displayed.
   *
   * @return The unique id of this localization.
   */
  public SIBUuid12 getUUID();

  /**
   * Allows the caller to find out whether this localization accepts messages
   *
   * @return false if the localization prevents new messages being sent, true
   * if further messages may be sent.
   */
  public boolean isSendAllowed();

  /**
   * Allows the caller to stop this localization accepting further messages
   * or not, depending on the value.
   * <p>
   * This has meaning for queueing point
   *
   * @param arg true if messages are to be allowed onto this localization,
   * false if messages are to be prevented being put onto this
   * localization.
   */
  public void setSendAllowed(boolean arg);

  /**
   * Get a single delivery stream set. This exists if we are sending
   * or have sent messages to a remote queue.
   *
   * @return The delivery stream or null if it is non existent.
   */
  public SIMPPtoPOutboundTransmitControllable getPtoPOutboundTransmit();

}
