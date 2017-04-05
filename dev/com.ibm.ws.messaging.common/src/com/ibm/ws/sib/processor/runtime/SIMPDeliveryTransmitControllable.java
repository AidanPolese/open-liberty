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
 * ---------------  ------ -------- ------------------------------------------
 * 248030.1         170105 tpm      Creation: MBean extensions
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * Interface to manipulate a flow of outgoing messages from this
 * messaging engine.
 * This is the super interface for message flows that are request/response
 * (remote get) and also for message flows that are transmit only (remote put)
 * @author tpm100
 */
public interface SIMPDeliveryTransmitControllable extends SIMPControllable
{
  
  /**
   * The possible states for this stream (to be implemented by the 
   * streams themselves).
   */
  public static interface StreamState
  {
    public String toString();
    public int getValue();
  }
  
  /**
   * @return the StreamState of the stream
   * @author tpm
   */
  StreamState getStreamState();
  
  /**
   * @return the stream ID of this source stream.
   * @author tpm
   */
  SIBUuid12 getStreamID();
  
  /**
   * Return the health state of the stream set
   * 
   * @return HealthState    The state of the stream set
   */
  HealthState getHealthState();
  
}
