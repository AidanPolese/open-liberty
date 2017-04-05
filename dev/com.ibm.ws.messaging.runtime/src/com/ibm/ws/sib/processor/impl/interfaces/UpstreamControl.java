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
 * ---------------  ------ -------- -------------------------------------------
 * 180483.1         271003 tevans   Interface changes to facilitate remoteGet
 * 171905.28        031103 tevans   Pubsub GD/ME-ME target flows
 * 181796.1         051103 gatfora  New MS5 Core API
 * 171905.34        191103 astley   unicast flush and stream IDs
 * 171905.44        171203 astley   Stream IDs for pub/sub.
 * 181718.4         221203 gatfora  Move to the new UUID classes
 * 186256.1         301203 tevans   Refactor GD/stream/flush code
 * 171905.47        140104 rjnorris Add support for sendWindow
 * 194131           110304 rjnorris Changes for foreign bus support   
 * 198550           140404 rjnorris Add remoteBus and remoteDestUuid
 * 186484.19.2      050704 tevans   PtoP Request flush at source
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * 419906           080307 cwilkin  Remove Cellules
 * PM56596.DEV      032012 chetbhat Managing conflicting ticks
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

// Import required classes.
import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.utils.SIBUuid12;
import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * @author tevans
 */
/**
 * An interface class for the different types of inpt class.
 */

public interface UpstreamControl
{

  /**
   * Send a nack message upstream.
   * 
   * @param  startTick The start tick for the nack.
   * @param  endTick The end tick for the nack.
   * @param  priority The priority within the stream that the nack refers to.
   * @param  reliability The reliability within the stream that the nack refers to.
   * @param  stream The stream ID that this nack refers to.  Tihs field is also
   * used to determine the cellule where the message will be sent.
   */
  public void sendNackMessage(SIBUuid8   source,
                              SIBUuid12 destUuid,
                              SIBUuid8  busUuid,  
                              long startTick,
                              long endTick,
                              int priority,
                              Reliability reliability,
                              SIBUuid12 streamID)
    throws SIResourceException;

  /**
   * Send a nack message upstream.
   * 
   * @param  startTick The start tick for the nack.
   * @param  endTick The end tick for the nack.
   * @param  priority The priority within the stream that the nack refers to.
   * @param  reliability The reliability within the stream that the nack refers to.
   * @param  stream The stream ID that this nack refers to.  Tihs field is also
   * used to determine the cellule where the message will be sent.
   */
  public long sendNackMessageWithReturnValue(SIBUuid8   source,
                              SIBUuid12 destUuid,
                              SIBUuid8  busUuid,  
                              long startTick,
                              long endTick,
                              int priority,
                              Reliability reliability,
                              SIBUuid12 streamID)
    throws SIResourceException;
  

  /**
   * Send an ack message upstream.
   * 
   * @param ackPrefix The prefix for the ack.
   * @param priority The priority within the stream that the ack refers to.
   * @param reliability The reliability within the stream that the ack refers to.
   * @param stream The stream ID that this ack refers to.  This field is also used
   * to determine the cellule where the message will be sent.
   * @throws SIResourceException
   */
  public void sendAckMessage(SIBUuid8   source,
                             SIBUuid12 destUuid,
                             SIBUuid8  busUuid,  
                             long ackPrefix,
                             int priority,
                             Reliability reliability,
                             SIBUuid12 streamID,
                             boolean consolidate)
    throws SIResourceException;

  public void sendAreYouFlushedMessage(SIBUuid8   source,
                                       SIBUuid12 destUuid,
                                       SIBUuid8  busUuid,  
                                       long queryID,
                                       SIBUuid12 streamID)
    throws SIResourceException;
  
  public void sendRequestFlushMessage(SIBUuid8   source,
                                      SIBUuid12 destUuid,
                                      SIBUuid8  busUuid,  
                                      long queryID,
                                      SIBUuid12 streamID,
                                      boolean indoubtDiscard)
      throws SIResourceException;

}
