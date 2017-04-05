/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 279076           290905 tevans   Reset Change history - previous WAS602.SIB
 * 279076           290905 tevans   Add deleteAllQueuedMessages
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * SIB0113a.mp.10   231107 cwilkin  Message Gathering controllables
 * 515543           180708 cwilkin  Handle MessageStoreRuntimeExceptions on msgstore interface
 * 673411           240211 urwashi  Added new overloaded method getQueuedMessageIterator(fromIndex,toIndex,totalMessages)
 * ======================================================================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.*;
import com.ibm.ws.sib.processor.exceptions.SIMPException;

/**
 * The interface presented by a queueing point localization to perform dynamic
 * control operations.
 * <p>
 * The operations in this interface are specific to a queueing point.
 */
public interface SIMPLocalQueuePointControllable extends SIMPControllable
{
  /**
   * Returns the high messages limit property.
   *
   * @return The destination high messages threshold for this localization.
   */
  public long getDestinationHighMsgs();

  /**
   * Returns the low messages limit property.
   *
   * @return The destination low messages threshold for this localization.
   */
  public long getDestinationLowMsgs();

  /**
   * Get the parent Message Handler
   * @return The SIMPMessageHandlerControllable to which this Local Queue Point belongs.
   */
  public SIMPMessageHandlerControllable getMessageHandler();

  /**
   * Allows the caller to find out whether this localization accepts messages
   *
   * @return false if the localization prevents new messages being put, true
   * if further messages may be put.
   */
  public boolean isSendAllowed();
  /**
   * Allows the mbean user to set the current destination high messages threshold.
   * This value is not persisted.
   *
   * @param arg The high messages threshold for this localization.
   */
  public void setDestinationHighMsgs(long arg);
  /**
   * Allows the mbean user to set the current destination low messages threshold.
   * This value is not persisted.
   *
   * @param arg The low messages threshold for this localization.
   */
  public void setDestinationLowMsgs(long arg);
  /**
   * Allows the caller to stop this localization accepting further messages
   * or not, depending on the value.
   * <p>
   * This has meaning for both a queueing point
   *
   * @param arg true if messages are to be allowed onto this localization,
   * false if messages are to be prevented being put onto this
   * localization.
   */
  public void setSendAllowed(boolean arg);

  /**
   * Get an iterator over all of the messages on this local queue.
   * @return an iterator containing SIMPQueuedMessageControllable objects.
   */
  public SIMPIterator getQueuedMessageIterator()
    throws SIMPInvalidRuntimeIDException,
    SIMPControllableNotFoundException,
    SIMPException;

  public SIMPQueuedMessageControllable getQueuedMessageByID(String ID)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;
  public long getNumberOfQueuedMessages();

  /**
   * Move the messages currently on this queue point to the exception dest. If an exception
   * occurs trying to delete a particular message, it will be remembered and
   * rethrown at the end, after an attempt has been made to delete the rest. If
   * discard is true, delete them
   *
   * @param discard If true, throw all the messages away.
   *                If false, move the messages to the exception destination.
   * @throws SIMPRuntimeOperationFailedException
   * @throws SIMPControllableNotFoundException
   */
  public void moveMessages(boolean discard) throws SIMPRuntimeOperationFailedException, SIMPControllableNotFoundException;

  /**
   * Get an iterator over all of the inbound receivers for this local queue.
   * @return an iterator containing SIMPInboundReceioverControllable : one for each
   * remote ME that delivers to this queue
   * objects.
   */
  public SIMPIterator getPtoPInboundReceiverIterator();

  /**
   * Get an iterator over the remote browser receiver
   *
   * @return Iterator over SIMPRemoteBrowserReceiverControllable
   */
  SIMPIterator getRemoteBrowserReceiverIterator();

	/**
	 * Get an iterator over the remote consumer transmit for each ME.
	 * This exists if a remote get has been performed against this local queue
   *
   * As of SIB0113 we now have multiple remote consumer transmitters per ME.
   * The non gathering version of this method is used for legacy code.
	 *
	 * @return A Iterator over SIMPRemoteConsumerTransmitControllable
	 */
	SIMPIterator getNonGatheringRemoteConsumerTransmitIterator();

  /**
   * Get an iterator over the remote consumer transmitters for each ME.
   * This exists if a remote get has been performed against this local queue
   *
   * As of SIB0113 we now have multiple remote consumer transmitters per ME.
   * The non gathering version of this method is used for legacy code.
   *
   * @return A Iterator over SIMPRemoteConsumerTransmitControllable
   */
  SIMPIterator getAllRemoteConsumerTransmitIterator();

// 673411
 /**
  * Get an iterator over a range of the messages on this local queue starting from fromIndex to Index
  * @return an iterator containing SIMPQueuedMessageControllable objects.
  */
public SIMPIterator getQueuedMessageIterator(int toIndex,int fromIndex,int totalMessages)throws SIMPInvalidRuntimeIDException,SIMPControllableNotFoundException,SIMPException;
// 673411
}
