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
 * ---------------  ------ -------- ------------------------------------------
 * 248030.1         170105 tpm      Creation: MBean extensions
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * 413848           250107 cwilkin  Propogate Move/Reallocate exceptions to admin
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.runtime;

import com.ibm.websphere.sib.Reliability;
import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.exceptions.SIMPRuntimeOperationFailedException;

/**
 * Interface to manipulate an individual stream of outgoing messages from this
 * messaging engine.
 * This is for transmitted (remote put) messages as opposed to 
 * requested (remote get) messages.
 * @author tpm100
 */
public interface SIMPDeliveryStreamTransmitControllable extends SIMPDeliveryTransmitControllable
{
  
  /**
   * @return the reliability of this source stream.
   * @author tpm
   */
  Reliability getReliability();
  
  /**
   * @return the priority of this source stream.
   * @author tpm
   */
  int getPriority();
  
  /**
   * @return the number of active messages on the source stream.
   * @author tpm
   */
  int getNumberOfActiveMessages();
  
  /**
   * @param id
   * @return a SIMPTransmitMessageControllable for the message with
   * the specified ID
   * @throws SIMPRuntimeOperationFailedException
   * @author tpm
   */
  SIMPTransmitMessageControllable getTransmitMessageByID(String id) 
    throws SIMPRuntimeOperationFailedException;
  
  /**
   * @return a SIMPIterator that enumerates the active messages awaiting
   * transmission.
   * Throws up SIMPTransmitMessageControllable
   * @author tpm
   */
  SIMPIterator getTransmitMessagesIterator(int maxMsgs);
  
  /**
   * @return a long for the total number of messages that have been
   * sent from this particular stream
   * @author tpm
   */
  long getNumberOfMessagesSent();
  
  /**
   * Move the message (either acked or unacked) with the corresponding ID.
   * If discard is set to true, delete it - else move to the exception destination
   * @param msgId
   * @param discard
   */  
  void moveMessage(String msgId, boolean discard) 
  throws SIMPRuntimeOperationFailedException, SIMPControllableNotFoundException;  
  

}
