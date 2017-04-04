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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.17.1      070704 ajw      Continue anycast runtime admin impl
 * 248030.1         170105 tpm      MBean extensions
 * 316556           251005 gatfora  Should use exported processor package for State Strings
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.SIMPConstants;


/**
 * An interface to manipulate a message request that we have received
 * from a remote messaging engine
 */
public interface SIMPTransmitMessageRequestControllable extends SIMPRemoteMessageControllable
{
  public static class State
  {
    // A message being remotely got at a DME. 
    // Defined known states types.
    public static final State REQUEST = new State(0, SIMPConstants.REQUEST_STRING);
    public static final State PENDING_ACKNOWLEDGEMENT = new State(1, SIMPConstants.PENDINGACKMR_STRING);
    public static final State ACKNOWLEDGED = new State(2, SIMPConstants.ACKNOWLEDGED_STRING);
    public static final State REMOVING = new State(3, SIMPConstants.REMOVING_STRING);
    public static final State REJECT = new State(4, SIMPConstants.REJECT_STRING);
    
    private int value;
    private String name;
    
    private static final State[] set = new State[]
      {REQUEST, PENDING_ACKNOWLEDGEMENT, ACKNOWLEDGED, REMOVING, REJECT};
    
    private State(int value, String name)
    {
      this.value = value;
      this.name = name;
    }
    
    public int toInt()
    {
      return value;      
    }
    
    public String toString()
    {
      return name;
    }
    
    public State getState(int value)
    {
      return set[value];
    }
  }
  
  long getTick();
  
  /**
   * @return SIMPReceivedMessageRequestInfo containing information about the
   * received message request.
   * @author tpm
   */
  SIMPReceivedMessageRequestInfo getRequestMessageInfo();
  
  /**
   * Cancels the message request.
   * @param discard if true, the message being requested is also deleted from
   * this local queue. Otherwise the message is simply unlocked and is
   * made available to other consumers
   * 
   * @author tpm
   */
  void cancelMessageRequest(boolean discard);
  
 
   
}
