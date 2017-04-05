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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.16        220604 tevans   Xmit Queue runtime control
 * 316556           251005 gatfora  Should use exported processor package for State Strings
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import java.util.Date;
import com.ibm.ws.sib.processor.SIMPConstants;



/**
 * An interface to manipulate a message on a stream
 * @author tpm100
 */
public interface SIMPTransmitMessageControllable extends SIMPRemoteMessageControllable
{
  public static class State
  {
    // A message meing sent to a DME.
    // Defined known states types.
    //Complete should not get used because we're not really interested
    //in completed transmit messages
    public static final State COMPLETE = new State(0, SIMPConstants.COMPLETE_STRING);
    public static final State COMMITTING = new State(1, SIMPConstants.COMMITTING_STRING);
    public static final State PENDING_SEND = new State(2, SIMPConstants.PENDINGSEND_STRING);
    public static final State PENDING_ACKNOWLEDGEMENT = new State(3, SIMPConstants.PENDINGACK_STRING);
    
    private int value;
    private String name;
    
    private static final State[] set = new State[]
      {COMPLETE, COMMITTING, PENDING_SEND, PENDING_ACKNOWLEDGEMENT};
    
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
    
  /**
   * @return the java.util.Date of when this message was produced
   * @author tpm
   */  
  Date getProducedTime();    
  
  /**
   * @return the value tick for this message
   * @author tpm
   */
  long getSequenceID();
  
  /**
   * @return a long for the value tick of the previous message
   * @author tpm
   */
  long getPreviousSequenceId();
}
