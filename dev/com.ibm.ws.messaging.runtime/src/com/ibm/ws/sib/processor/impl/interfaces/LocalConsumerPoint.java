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
 * SIB0002.mp.1     210605 tevans   PEV Prototype
 * SIB0002.tran.2   050805 tevans   New Transactions interfaces
 * 355323           220306 tevans   RMQSessionDroppedException handling
 * 358344           280306 tpm      Comments for new exception handling
 * 409469           281206 tevans   Refactor LME
 * SIB0115.mp.1     260407 ajw      Support pausing/resuming of a messaging endpoint
 * SIB0163.mp.4     081107 nyoung   Support the driving of Message Events.
 * SIB0115.mp.3     290108 dware    Add hiddenMesasgeDelay to stoppable callback registration
 * 496144           180608 cwilkin  Forward port PK58940
 * PK73713          161008 ajw      Allow messageset to be unlocked and not increased lock count
 * F013661          200412 chetbhat unlockAll(incrementUnlockCount) support
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.processor.exceptions.SIMPMessageNotLockedException;
import com.ibm.ws.sib.processor.impl.BifurcatedConsumerSessionImpl;
import com.ibm.ws.sib.processor.impl.ConsumerSessionImpl;
import com.ibm.ws.sib.processor.impl.MessageProcessor;
import com.ibm.ws.sib.processor.impl.OrderingContextImpl;
import com.ibm.ws.sib.transactions.TransactionCallback;
import com.ibm.ws.sib.transactions.TransactionCommon;
import com.ibm.wsspi.sib.core.AsynchConsumerCallback;
import com.ibm.wsspi.sib.core.StoppableAsynchConsumerCallback;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.SIMessageHandle;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

public interface LocalConsumerPoint extends ConsumerPoint, TransactionCallback
{
  //NO_WAIT is a special timeout value used by receive. It means that
  //receive should not wait at all for a message and should return
  //immediately after the first check. Note that a timeout value of
  //0L does not mean NO_WAIT ... 0L means an infinite wait, as defined by
  //Object.wait(long);
  final static long NO_WAIT = -1L;
  final static long INFINITE_WAIT = 0L;

  /**
   * <p>Performs a synchronous receive.</p>
   * <p>A positive timeout causes this method to check
   * once for a message and then wait for that amount of time, during which it may
   * be woken up when a message becomes available. However, by the time we actually
   * try and get hold of that message, it may have already gone so we go back and
   * continue to wait out any remaining time.</p>
   * <p>A timeout of zero causes this method to wait indefinitely until a message
   * is obtained.</p>
   * <p>A timeout of NO_WAIT (-1) causes this method to just check once and return
   * without waiting at all</p>
   * 
   * @param timeout The time to wait for a message, in milliseconds
   * @param transaction The transaction to be used to get messages from the msgStore.
   * If this is specified as null, internal LocalTransaction is created for the job.
   * @return The obtained message, if any
   * AsynchConsumerCallback registered when the recieve is attempted
   * @throws SIResourceException Thrown if a problem occurs in the msgStore.
   */
  public JsMessage receive(long originalTimeout, TransactionCommon transaction)
      throws SISessionUnavailableException, SIIncorrectCallException,
      SIResourceException, SINotPossibleInCurrentConfigurationException;

  /**
   * Register an AsynchConsumerCallback. This creates the AsynchConsumer wrapper
   * if needed. If there was a callback already registered then it first attempts
   * to process any existing attached messages on that callback.
   * 
   * @param callback The AsynchConsumerCallback to be registered
   * @param maxBatchSize The maximum number of messages to be passed on each callback
   * @param optionalCallbackBusyLock A lock which should be held when the callback
   * is busy, or to prevent the callback from becoming busy.
   * @throws SIResourceException Thrown if there was a problem in the msgStore
   * receive right now
   */
  public void registerAsynchConsumer(AsynchConsumerCallback callback,
      int maxActiveMessages, long messageLockExpiry, int maxBatchSize,
      Reliability unrecoverableReliability, boolean inLine,
      OrderingContextImpl orderingGroup,
      ExternalConsumerLock optionalCallbackBusyLock)
      throws SISessionUnavailableException, SISessionDroppedException,
      SIErrorException, SIIncorrectCallException;
  
  /**
   * Register an StoppableAsynchConsumerCallback. This creates the AsynchConsumer wrapper
   * if needed. If there was a callback already registered then it first attempts
   * to process any existing attached messages on that callback.
   * 
   * @param callback The AsynchConsumerCallback to be registered
   * @param maxBatchSize The maximum number of messages to be passed on each callback
   * @param optionalCallbackBusyLock A lock which should be held when the callback
   * is busy, or to prevent the callback from becoming busy.
   * @param maxSequentialFailures The maximum bumber of sequential failures allowed until
   * the consumer session is stopped.
   * @param hiddenMessageDelay Time (in milliseconds) that a message is hidden for before
   * it is re-available to a consumer (ignored if maxSequentialFailures is zero)
   * @throws SIResourceException Thrown if there was a problem in the msgStore
   * receive right now
   */
  public void registerStoppableAsynchConsumer(
      StoppableAsynchConsumerCallback callback,
      int maxActiveMessages,
      long messageLockExpiry,
      int maxBatchSize,
      Reliability unrecoverableReliability,
      boolean inLine,
      OrderingContextImpl orderingGroup,
      ExternalConsumerLock optionalCallbackBusyLock,
      int maxSequentialFailures,
      long hiddenMessageDelay) 
    throws SISessionUnavailableException, SISessionDroppedException,
           SIErrorException,
           SIIncorrectCallException;

  /**
   * Start this LCP. If there are any synchronous receives waiting, wake them up
   * If there is a AsynchConsumerCallback registered look on the QP for messages
   * for asynch delivery. If deliverImmediately is set, this Thread is used to deliver
   * any initial messages rather than starting up a new Thread.
   */
  public void start(boolean deliverImmediately)
      throws SISessionUnavailableException;

  public void stop() throws SISessionUnavailableException, SIResourceException;
  
  /**
   * Update the max active messages field
   * 
   * Set by the MessagePump class to indicate that there has been an update
   * in the Threadpool class.
   *  
   * @param maxActiveMessages
   */
  public void setMaxActiveMessages(int maxActiveMessages);
  
  public ConsumerSessionImpl getConsumerSession();

  /**
   * @param consumer
   * @throws SIResourceException
   * @throws SISessionDroppedException 
   */
  public void cleanupBifurcatedConsumer(BifurcatedConsumerSessionImpl consumer) throws SIResourceException, SISessionDroppedException;

  /**
   * @throws SINotPossibleInCurrentConfigurationException
   * @throws SIResourceException
   * 
   */
  public void close() throws SIResourceException, SINotPossibleInCurrentConfigurationException;

  /**
   * @throws SISessionUnavailableException
   * 
   */
  public void checkNotClosed() throws SISessionUnavailableException;

  /**
   * Unlock/delete/read a set of locked messages based on their short ID (which
   * is unique to this LCP). Warning: The array must have at least one entry!
   * 
   * @param msgIds
   */
  public SIBusMessage[] processMsgSet(SIMessageHandle[] msgHandles, 
                                         TransactionCommon transaction,
                                         BifurcatedConsumerSessionImpl owner,
                                         boolean unlock,
                                         boolean delete,
                                         boolean read,
                                         boolean incrementLockCount) 
    throws SISessionUnavailableException, SIMPMessageNotLockedException, SIConnectionLostException, SIIncorrectCallException, SIResourceException, SIErrorException;

  /**
   * @throws SIResourceException
   * @throws SISessionUnavailableException
   * @throws SIMPMessageNotLockedException 
   * @throws SIMPMessageNotLockedException 
   * 
   */
  public void unlockAll() throws SISessionUnavailableException, SIResourceException, SIMPMessageNotLockedException;

  /**
   * Run the asynch consumer once only when the session is stopped.
   * 
   * @param deliverImmediately
   * @throws SIResourceException thrown if there is a problem in the message store
   * @throws SISessionUnavailableException
   * @throws SIIncorrectCallException
   */
  public void runIsolatedAsynch(boolean deliverImmediately) throws SIIncorrectCallException, SISessionUnavailableException, SIResourceException;

  /**
   * @return
   * @throws SISessionUnavailableException
   * @throws SIResourceException
   */
  public SIBusMessage relockMessageUnderAsynchCursor() throws SISessionUnavailableException;
  
  /**
   * Called when an exception occurs that should be flowed to the
   * exception listeners. It expects the consumer point to have been closed before hand
   * @param exception
   */
  public void notifyException(Throwable exception);
  
  public MessageProcessor getMessageProcessor();

  public void removeActiveMessages(int i);

  public boolean isCountingActiveMessages();

  
  /**
   * Retrieve the label of the ConsumerSet that this consumer belongs to. The label will be null if the consumer
   * is not classifying messages for a registered MessageController such as XD.
   * 
   * @return label if we are classifying messages
   */
  public String getConsumerSetLabel();  
  
  /**
   * @throws SIResourceException
   * @throws SISessionUnavailableException
   * @throws SIMPMessageNotLockedException 
   * @throws SIMPMessageNotLockedException 
   * 
   */
  public void unlockAll(boolean incrementUnlockCount) throws SISessionUnavailableException, SIResourceException, SIMPMessageNotLockedException;

}
