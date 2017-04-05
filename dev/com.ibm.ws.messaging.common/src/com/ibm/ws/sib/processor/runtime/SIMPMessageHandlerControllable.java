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
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 201691           050504 gatfora  More javadoc corrections.
 * 186484.10        170504 tevans   MBean Registration
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import java.util.Map;

import com.ibm.ws.sib.utils.SIBUuid12;

/**
 * 
 */
public interface SIMPMessageHandlerControllable extends SIMPControllable
{
  /**
   * Locates the Message Processor relating to the known destination. 
   *
   * @return SIMPMessageProcessorControllable  The Message Processor object. 
   */
  SIMPMessageProcessorControllable getMessageProcessor();
  
  /**
   * Get the browsers directly addressing this message handler. 
   *
   * @return An iterator over all of the SIMPBrowserControllable objects. 
   */
  public SIMPIterator getBrowserIterator();
  /**
   * Get the consumers directly addressing this message handler. 
   *
   * @return An iterator over all of the SIMPConsumerControllable objects.
   */
  public SIMPIterator getConsumerIterator();
  /**
   * Get the producers directly addressing this message handler. 
   *
   * @return An iterator over all of the SIMPProducerControllable objects. 
   */
  public SIMPIterator getProducerIterator();  

  
  /**
   * Is this destination a local destination, defined on this bus?
   * @return true if this is local
   */
  public boolean isLocal();

  /**
   * Is this destination an alias to another destination, possibly defined
   * on this bus?
   * @return true if this is an alias
   */
  public boolean isAlias();

  /**
   * Determines if the destination is a system destination.  
   * @return true if the queue is a system destination. 
   */
  public boolean isSystem();

  /**
   * Determines if the destination is a temporary destination.  
   * @return true if the queue is a temporary destination. 
   */
  public boolean isTemporary();

  /**
   * Determines if the state of a destination.  
   * @return the state of a destination. 
   */
  public String getState();

  /**
   * Is this a foreign destination, indicating that the referenced destination
   * on the foreign bus exists?
   * @return true if this is foreign.
   */
  public boolean isForeign();

  public String getDescription();
  public Map getDestinationContext();
  public SIBUuid12 getUUID();
  
}
