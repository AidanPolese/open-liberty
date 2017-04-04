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
 * 186484.3         220304 tevans   Further controllable interfaces
 * 186484.5         220404 ajw      Further Continued controllable interfaces
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.7         290404 tevans   More runtime control interfaces and implementation
 * 186484.10        170504 tevans   MBean Registration
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * 316556           251005 gatfora  Should use exported processor package for State Strings
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * SIB0115.mp.3     230108 nyoung   Hidden message expiry and pending_retry support
 * SIB0115.mp.3     290108 dware    Add BLOCKED state to queued messages
 * 522345           200508 dware    Add new locked states
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.exceptions.*;

public interface SIMPQueuedMessageControllable extends SIMPControllable
{
  public JsMessage getJsMessage() throws SIMPControllableNotFoundException, SIMPException;
  public String getState() throws SIMPException;
  /**
   * Get the transaction identifier XID of the transaction commiting the message or null 
   * if the message is already in commited state.
   *
   * @return a Transaction ID
   */  
  public String getTransactionId() throws SIMPException;
    
  /**
   * If the destination contains a poison message that cannot be processed, for example
   *  because the message is too big for to handle the administrator may 
   *  instruct us to discard the message. Optionally 
   *  the message is transferred to the exception destination. 
   *  This request is atomic and the message will have been moved by the time this 
   *  method returns.
   *     
   * @param discard  true of the message is to be discarded. 
   *                 false if the message is to be sent to the exception destination.
   */
  void moveMessage(boolean discard)
    throws SIMPControllableNotFoundException,
           SIMPRuntimeOperationFailedException;
  
  public static class State
  {
    public static final String LOCKED = SIMPConstants.LOCKED_STRING;
    public static final String UNLOCKED = SIMPConstants.UNLOCKED_STRING;
    public static final String PENDING_RETRY = SIMPConstants.PENDING_RETRY_STRING;
    public static final String BLOCKED = SIMPConstants.BLOCKED_STRING;
    public static final String COMMITTING = SIMPConstants.COMMIT_STRING;
    public static final String REMOVING = SIMPConstants.REMOVE_STRING;
    public static final String REMOTE_LOCKED = SIMPConstants.REMOTE_LOCKED_STRING;
  }
  
  /**
   * @return the value tick for this message
   * @author tpm
   */
  public long getSequenceID();
  
  /**
   * @return a long for the value tick of the previous message
   * @author tpm
   */
  public long getPreviousSequenceId();
  
  /**
   * Get the approximate length (bytes) of the queued message
   * @return length in bytes
   */
  public long getApproximateLength();
}
