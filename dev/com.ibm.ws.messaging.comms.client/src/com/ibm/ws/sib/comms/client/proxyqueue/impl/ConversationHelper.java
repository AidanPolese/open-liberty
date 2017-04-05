/*
 * @start_prolog@
 * Version: @(#) 1.30 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/ConversationHelper.java, SIB.comms, WASX.SIB, uu1215.01 08/01/31 05:31:31 [4/12/12 22:14:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008
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
 * Creation        030702 prestona Original
 * F171893         030721 prestona Add BrowserSession support on client.
 * F174602         030819 prestona Switch to using SICommsException
 * f174317         030827 mattheg  Add local transaction support
 * f173765.2       030925 mattheg  Core API M4 update
 * f177889         030929 mattheg  Core API M4 completion
 * d178368         031008 mattheg  Ensure delete set is flowed at the correct priority
 * f187521.2.1     040126 mattheg  Unrecoverable reliability -- part 2
 * f200337         040428 mattheg  Message order context implementation
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * F219476.2       040906 prestona Z3 Core SPI changes
 * D268606         050415 mattheg  Add method to expose comms connection
 * D307265         050918 prestona Support for optimized transactions
 * D365952         060523 mattheg  Add support for SIMessageNotLockedException
 * SIB0115d.comms  070928 vaughton StoppableAsynchConsumerCallback
 * 472879          071008 vaughton StoppableAsynchConsumerCallback confusion
 * SIB0115.comms.2 080131 vaughton Update registerStoppableAsynchConsumerCallback
 * ============================================================================
 */

package com.ibm.ws.sib.comms.client.proxyqueue.impl;

import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.comms.CommsConnection;
import com.ibm.wsspi.sib.core.AsynchConsumerCallback;
import com.ibm.wsspi.sib.core.OrderingContext;
import com.ibm.wsspi.sib.core.SIMessageHandle;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SIMessageNotLockedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;


/**
 * Interface which defines the conversation related operations
 * required to communicate with the ME.  By splitting this interface
 * out from the implementation, we can substitute alternative
 * implementations for testing purposes.
 */
public interface ConversationHelper
{
   /**
    * Request the session to be closed.
    */
   void closeSession()
      throws SIResourceException, SIConnectionLostException,
             SIErrorException, SIConnectionDroppedException;

   /**
    * Delete a set of messages.
    * @param msgIds The message IDs for the messages to delete.
    * @param tran   The transaction to delete the messages under.
    * @param priority The JFAP priority to send the message.
    */
   void deleteMessages(SIMessageHandle[] msgHandles, SITransaction tran, int priority)         // f174317, d178368, F219476.2
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, SILimitExceededException,
             SIIncorrectCallException, SIMessageNotLockedException,
             SIErrorException;

   /**
    * Flushes the consumer session in an attempt to dislodge
    * any messages.
    */
   void flushConsumer()
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException,
             SIErrorException;

   /**
    * Request more messages (used by read ahead queues)
    * @param receivedBytes Number of bytes received.
    * @param requestedBytes Number of bytes requested.
    */
   void requestMoreMessages(int receivedBytes, int requestedBytes)
      throws SIConnectionDroppedException, SIConnectionLostException;

   /**
    * Request the session starts.
    */
   void sendStart()
      throws SIConnectionDroppedException, SIConnectionLostException;

   /**
    * Request the session stops.
    */
   void exchangeStop()
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException,
             SIErrorException;

   /**
    * Sets an asycnhronous consumer callback.
    */
   void setAsynchConsumer(AsynchConsumerCallback consumer,                 // f177889
                          int maxActiveMessages,
                          long messageLockExpiry,                          // F219476.2
                          int maxBatchSize,                                // f177889  // f187521.2.1
                          OrderingContext orderContext,                    // f200337
                          int maxSequentialFailures,                       // SIB0115d.comms
                          long hiddenMessageDelay,
                          boolean stoppable)                               // 472879
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIErrorException,
             SIIncorrectCallException;

   /**
    * Sets the session ID
    * @param sessionId
    */
   void setSessionId(short sessionId);

   /**
    * Requests an unlock all.
    * @throws SIInvalidStateForOperationException
    * @throws SIResourceException
    */
   void unlockAll()
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException,
             SIErrorException;

   /**
    * Unsets an asynch consumer.
    */
   void unsetAsynchConsumer(boolean stoppable)                                                          //SIB0115d.comms
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIErrorException,
             SIIncorrectCallException;

   /**
    * Unlocks a set of messages.
    * @param msgIds The message Ids to unlock.
    */
   void unlockSet(SIMessageHandle[] msgHandles)                            // F219476.2
      throws SIIncorrectCallException, SIMessageNotLockedException,
             SIConnectionDroppedException, SIConnectionLostException;

   /**
    * Requests another batch of messages (asynchronous queue).
    */
   void requestNextMessageBatch()
      throws SIConnectionDroppedException, SIConnectionLostException;

   /**
    * Exchanges a request for a reset of the browse cursor.
    */
   void exchangeResetBrowse()
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException,
             SIErrorException;

   /**
    * @return Returns the comms connection associated with this conversation.
    */
   CommsConnection getCommsConnection();                                                  // D268606
}
