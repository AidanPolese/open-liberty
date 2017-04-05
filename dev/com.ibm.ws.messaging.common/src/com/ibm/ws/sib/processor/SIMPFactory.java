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
 * 159093           070303 jroots   Original
 * 161135           170303 tevans   Add createMessagingEngine()
 * 161400           200303 tevans   Fix javadoc on messagingEngineExists
 * 161773           250303 tevans   Create SIMPFactory interface
 * 161877           260303 tevans   Mods to allow for bootstrapped MsgStore & renamed MP
 * 162915           080403 tevans   Make the Core API code look like the model
 * 163636           160403 tevans   Upgrade to 0.4 model
 * 165679           080503 tevans   Upgrade to 0.4b model
 * 166828           060603 tevans   Core MP rewrite
 * 171905.6         110803 tevans   Remote flows
 * 177630           240903 tevans   Add isStarted()
 * 178888           141003 cwilkin  Add createExceptionDestinationHandler()
 * 181796.1         051103 gatfora  New MS5 Core API
 * 182754           211103 tevans   Remove the MessageReceiver/Transmitter interfaces
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * 325992           231105 cwilkin  Use destinationAddress for exception dests
 * 460808           131207 cwilkin  Enable exception destinations for MQLinks
 * ===========================================================================
 */

package com.ibm.ws.sib.processor;

import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.sib.comms.MEConnectionListener;
import com.ibm.ws.sib.trm.topology.TopologyListener;
import com.ibm.ws.sib.utils.SIBUuid8;



/**
 * @author jroots
 */
public interface SIMPFactory
{

  /**
   * return a new reference to an implementation of ExceptionDestinationHandler.
   * Accepts a name of a destination that could not be delivered to OR null if there
   * is no destination.
   *
   * @param destName - The name of the destination that could not be delivered to.
   */
  public ExceptionDestinationHandler createExceptionDestinationHandler(SIDestinationAddress dest)
    throws SIException;

  public ExceptionDestinationHandler createLinkExceptionDestinationHandler(SIBUuid8 mqLinkUuid)
  throws SIException;

  public MEConnectionListener getMEConnectionListener();
  public TopologyListener getTopologyListener();
  public boolean isStarted();
}
