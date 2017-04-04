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
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 203379           140504 gatfora  Removal of compile time warnings.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

/**
 * The connection controllable interface
 */
public interface SIMPConnectionControllable extends SIMPControllable
{
  /**
   * 
   * @return SIMPMessageProcessorControllable  to perform dynamic control operations
   */
  SIMPMessageProcessorControllable getMessageProcessor();

  /**
   * Locates the consumers created under this connection. 
   *
   * @return Iterator  An iterator over all of the Consumer objects. 
   *
   */
  SIMPIterator getConsumerIterator();

  /**
   * Locates the producers created under this connection. 
   *
   * @return Iterator  An iterator over all of the Producer objects. 
   */
  SIMPIterator getProducerIterator();

  /**
   * Locates the browsers created under this connection. 
   *
   * @return Iterator  An iterator over all of the Browser objects. 
   *
   */
  SIMPIterator getBrowserIterator();
}
