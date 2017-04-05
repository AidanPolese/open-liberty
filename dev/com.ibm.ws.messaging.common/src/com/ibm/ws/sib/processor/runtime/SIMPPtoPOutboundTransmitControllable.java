/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Materials
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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.22        090704 cwilkin  implement getTransmitMessageByID
 * 248030.1         240105 tpm      MBean extensions
 * 248030.1.1       160205 tpm      Reallocate
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * 413848           250107 cwilkin  Propogate Move/Reallocate exceptions to admin
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.exceptions.SIMPRuntimeOperationFailedException;

/**
 * A transmit stream set for PtoP messages
 */
public interface SIMPPtoPOutboundTransmitControllable extends SIMPDeliveryStreamSetTransmitControllable
{
  /**
   * Provides an iterator over all of the tick ranges in all of the streams in the set. 
   *  The messages are listed for each stream in turn and in the order that they are
   *  saved in the stream starting with the oldest message at the head of the stream.
   *
   * @return Iterator An iterator over the messages in the stream. The iterator contains a set of 
   *          DeliveryStreamTickRange objects in ascending tick order starting with the oldest
   *          for each class of service and priority. All ticks for one class of service and priority
   *          are returned before the next class of service and priority. 
   *
   */
  SIMPIterator getTransmitMessagesIterator(int maxMsgs)
    throws SIMPRuntimeOperationFailedException, SIMPControllableNotFoundException;
  
  /**
   * Returns the message on the outbound transmission stream with the 
   * given id.
   * @param id
   * @return the transmit message
   */
  
  SIMPTransmitMessageControllable getTransmitMessageByID(String id)
    throws SIMPRuntimeOperationFailedException, SIMPControllableNotFoundException;
    
  /**
   * Reallocates all messages on the outbound streams so that they
   * are sent to a different localization.
   */
  void reallocateAllTransmitMessages()
    throws SIMPRuntimeOperationFailedException, SIMPControllableNotFoundException;
  
}
