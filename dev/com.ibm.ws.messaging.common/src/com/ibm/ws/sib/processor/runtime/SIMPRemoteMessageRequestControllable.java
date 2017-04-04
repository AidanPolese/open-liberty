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
 * 186484.17        050704 ajw      anycast runtime admin impl
 * 215547           120704 ajw      Cleanup remote runtime admin control impl
 * 216685           160704 ajw      Cleanup anycast runtime control impl
 * 217348           190704 ajw      added State completed
 * 248030.1         170105 tpm      MBean extensions
 * 316556           251005 gatfora  Should use exported processor package for State Strings
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.exceptions.SIMPRuntimeOperationFailedException;

/**
 * Interface to manipulate a message request that we have sent to a 
 * remote messaging engine
 * 
 * @author tpm100
 */
public interface SIMPRemoteMessageRequestControllable extends SIMPRemoteMessageControllable
{
  public static class State
  {
		// A message being requested by a RME. 
		// Defined known states types.
		public static final State REQUEST = new State(0, SIMPConstants.REQUEST_STRING);
		public static final State VALUE = new State(1, SIMPConstants.VALUE_STRING);
		public static final State LOCKED = new State(2, SIMPConstants.LOCKED_MR_STRING);
		public static final State ACKNOWLEDGED = new State(3, SIMPConstants.ACKNOWLEDGED_STRING);
		public static final State REJECT = new State(4, SIMPConstants.REJECT_STRING);
        public static final State COMPLETED = new State(5, SIMPConstants.COMPLETED_STRING);
    
		private int value;
		private String name;
    
		private static final State[] set = new State[]
			{REQUEST, VALUE, LOCKED, ACKNOWLEDGED, REJECT, COMPLETED};
    
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
   * Returns the SIMPRequestMessageInfo if the state of the SIMPRemoteMessageRequest
   * is REQUEST else null
   * 
   * @throws SIMPRuntimeOperationFailedException 
   * @return SIMPRequestMessageInfo
   */
  SIMPRequestMessageInfo getRequestMessageInfo() throws SIMPRuntimeOperationFailedException;
  
  /**
   * Returns the SIMPRequestedValueMessageUInfo if the state of the SIMPRemoteMessageRequest
   * is VALUE else null.
   * 
   * @throws SIMPRuntimeOperationFailedException
   * @return SIMPRequestedValueMessageInfo
   */
  SIMPRequestedValueMessageInfo getRequestedValueMessageInfo() throws SIMPRuntimeOperationFailedException;
}
