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
 * 171905.17        031003 rjnorris intial implementation
 * 180483.1         271003 tevans   Interface changes to facilitate remoteGet
 * 181796.1         051103 gatfora  New MS5 Core API
 * 171905.34        191103 astley   unicast flush and stream IDs
 * 171905.39        031203 rjnorris Updates due to Pubsub code review
 * 171905.42        081203 prmf     GD Restart Recovery
 * 171905.41        091203 astley   Ptp concurrency change, bug fixes
 * 171905.44        171203 astley   Stream IDs for pub/sub.
 * 181718.4         221203 gatfora  Move to the new UUID classes
 * 186256.1         301203 tevans   Refactor GD/stream/flush code
 * 171905.46        200104 astley   pub/sub flush
 * 171905.47.1      020204 tevans   Source Stream Persistence
 * 199574           050405 gatfora  Remove use of ArrayLists.
 * 197856.1         170504 rjnorris Message expiry before send
 * 204576           110604 gatfora  Missing FFDC statements. 
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * 257530           080305 tpm      get value message
 * 419906           080307 cwilkin  Remove Cellules
 * 515543           180708 cwilkin  Handle MessageStoreRuntimeExceptions on msgstore interface
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.impl.interfaces;

import java.util.List;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.impl.store.items.MessageItem;
import com.ibm.ws.sib.utils.SIBUuid12;
import com.ibm.ws.sib.utils.SIBUuid8;

/**
 * An interface class for handling downstream messages
 */
public interface DownstreamControl
{

  public void sendAckExpectedMessage(
    long ackExpStamp,
    int priority,
    Reliability reliability,
    SIBUuid12 stream)
    throws SIResourceException;

   public void sendSilenceMessage(
    long startStamp,
    long endStamp,
    long completedPrefix,
    boolean requestedOnly,
    int priority,
    Reliability reliability,
    SIBUuid12 stream)
    throws SIResourceException;

  public List sendValueMessages(
    List msgList,
    long completedPrefix,
    boolean requestedOnly,
    int priority,
    Reliability reliability,
    SIBUuid12 stream)
    throws SIResourceException;
  
  /**
   * Retreive the message item from the store if it exists.
   * If there is an error retreiving the item or if the item has expired
   * then null is returned.
   */  
  public MessageItem getValueMessage(long msgStoreID)
    throws SIResourceException;
  
  public void sendFlushedMessage(SIBUuid8 target, SIBUuid12 streamID)
    throws SIResourceException;
    
  public void sendNotFlushedMessage(SIBUuid8 target, SIBUuid12 streamID, long requestID) throws SIResourceException;
}
