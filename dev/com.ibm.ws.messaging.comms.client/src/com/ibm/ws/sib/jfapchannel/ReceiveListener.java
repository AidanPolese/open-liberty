/*
 * @start_prolog@
 * Version: @(#) 1.15 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ReceiveListener.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:31:42 [4/12/12 22:14:11]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * F174602         030819 prestona Switch to using SICommsException
 * f181007         031211 mattheg  Add boolean 'exchange' flag on dataReceived()
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * Used as a notification that some data has been received from our peer.
 * This is as a result of a previous request which has been sent to and
 * solicitied a response from our peer.
 * @author prestona
 */
public interface ReceiveListener
{
   /**
    * Notification that data was received.
    * @param data The data.
    * @param segmentType   The segment type associated.
    * @param requestNumber The request number associated with this
    *                       transmission at send time.
    * @param priority The priority the data was sent with.
    * @param allocatedFromBufferPool The data received was placed into a buffer
    *                                 allocated from the WS buffer pool.
    * @param partOfExchange A hint to our peer that the data received was sent as the initiating 
    *                       part of an exchange and thus a reply will be expected.
    * @param conversation The conversation associated with the data received.
    */
   void dataReceived(WsByteBuffer data, 
                     int segmentType,
                     int requestNumber,
                     int priority,
                     boolean allocatedFromBufferPool,
                     boolean partOfExchange,                                              // f181007
                     Conversation conversation);
   
   /**
    * Notification that an error occurred when we were expecting to receive
    * a response.  This method is used to "wake up" any conversations using
    * a connection for which an error occurres.  At the point this method is
    * invoked, the connection will already have been marked "invalid".
    * <p>
    * Where this method is implemented in the ConversationReceiveListener
    * interface (which extends this interface) it is used to notify 
    * the per conversation receive listener of (almost) all error conditions
    * encountered on the associated connection.
    * @see ConversationReceiveListener
    * @param exception The exception which occurred.
    * @param segmentType The segment type of the data (-1 if not known)
    * @param requestNumber The request number associated with the failing
    *                       request (-1 if not known)
    * @param priority The priority associated with the failing request
    *                  (-1 if not known).
    * @param conversation The conversation (null if not known)
    */
   void errorOccurred(SIConnectionLostException exception,                                // F174602 
                      int segmentType, 
                      int requestNumber,
                      int priority,
                      Conversation conversation);
                      
}
