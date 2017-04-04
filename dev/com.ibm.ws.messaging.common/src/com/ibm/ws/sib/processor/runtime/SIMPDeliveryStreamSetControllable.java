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
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * SIB0105.mp.1     071106 cwilkin  Link Transmission Controllables
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * 
 */
public interface SIMPDeliveryStreamSetControllable extends SIMPControllable
{
  /**
   * All of the streams in a set are the same type.
   *
   * @return DeliveryStreamType  The type of stream, source target p2p etc.
   */
  DeliveryStreamType getType();  
  
  /**
   * Return the health state of the stream set
   * 
   * @return HealthState    The state of the stream set
   */
  HealthState getHealthState();
}
