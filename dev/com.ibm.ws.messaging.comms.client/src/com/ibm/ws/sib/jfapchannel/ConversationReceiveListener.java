/*
 * @start_prolog@
 * Version: @(#) 1.19 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ConversationReceiveListener.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:21:27 [4/12/12 22:14:11]
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
 * Creation        030521 prestona Original
 * F174776         030822 prestona Allow chaining of receive listeners
 * F174772         030901 prestona Make JFAP Channel support close.
 * F176003         030911 prestona Misc. JFAP Channel reliability fixes.
 * f181007         031211 mattheg  Add boolean 'exchange' flag on dataReceived()
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * F201521         040505 mattheg  New getThreadContext() method
 * D211250         040622 mattheg  Remove closeReceived() method
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199145         040812 prestona Fix Javadoc
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

/**
 * Provides notification of per conversation events.
 * @author prestona
 */
public interface ConversationReceiveListener                         // F174776
{
   /**
    * Notification that data was received.
    * @param data The data.
    * @param segmentType   The segment type associated.
    * @param requestNumber The request number associated with this
    *                       transmission at send time.
    * @param priority The priority the data was sent with.
    * @param allocatedFromBufferPool The dat received was placed into a buffer
    *                                 allocated from the WS buffer pool.
    * @param partOfExchange A hint to the peer that this data is the initiating part of an
    *                       exchange and that a reply is required.
    * @param conversation The conversation associated with the data received.
    * @return ConversationReceiveListener A different receive listener to use
    * for this and subsequent requests.  If a non-null value is returned, the
    * current request is routed to it (it will be invoked with the same arguments
    * as were just used to invoke this method) and it will be used for all
    * subsequent data on this conversation.
    */
   ConversationReceiveListener dataReceived(WsByteBuffer data,    // F174776
                                            int segmentType,
                                            int requestNumber,
                                            int priority,
                                            boolean allocatedFromBufferPool,
                                            boolean partOfExchange,                       // f181007
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
   void errorOccurred(SIConnectionLostException exception,                                // F174776 
                      int segmentType, 
                      int requestNumber,
                      int priority,
                      Conversation conversation);
   
   // Start F201521
   /**
    * This method provides the JFap user the oppurtunity to specify which thread they would like 
    * the data to be queued on. By default, JFap will queue all data for the same conversation on
    * the same thread. However, it may be reasonable that the JFap can execute data from the same
    * conversation at the same time as other data on that conversation.
    * <p>
    * As such, the implementor of this method should inspect the data passed in here to determine 
    * whether the data can be executed concurrently. If it can be, the returned object will be used
    * to store information about the dispatch queue rather than this information be stored 
    * internally in the JFap channel. The theory being, that when similar data arrives that can also
    * be processed as part of this concurrent work, the same Dispatchable instance can be returned 
    * and the data will be queued behind other similar data, rather than just all the data for the
    * Conversation.
    * <p>
    * The obvious application here is for Transactions. In this case, when the data is inspected
    * and found to be part of a specific transaction, it should be executed in order of all the 
    * data on that transaction. This could even be across multiple conversations. When it is 
    * ascertained that the data is part of a transaction, returning an instance of Dispatchable 
    * that is tied to the transaction will ensure that the Transaction work gets completed on the 
    * same thread.
    * <p>
    * The Dispatchable instance may return a Dispatchable instance that does not currently hold a
    * dispatch queue. In which case, the JFap channel will retrieve one and associate it with this
    * dispatchable object.
    * <p>
    * Returning null from this method will indicate to the JFap channel that it should queue the 
    * data by Conversation.
    * 
    * @param conversation The conversation associated with the data about to be dispatched.
    * @param data The data that is about to be dispatched.
    * @param segmentType The segment type of the data that is about to be dispatched.
    * 
    * @return Returns an instance of Dispatchable that contains a reference to the dispatch queue
    *         that data should be queued to or null if the JFap channel should decide.
    */
   Dispatchable getThreadContext(Conversation conversation,
                                 WsByteBuffer data, 
                                 int segmentType);
   // End F201521
}
