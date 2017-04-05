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
 *                  210605 tevans   Reset Change history - previous WAS602.SIB
 *                  210605 tevans   Renamed original 602 file to DispatchableConsumerPoint
 * SIB0002.mp.1     210605 tevans   PEV Prototype
 * 355323           220306 tevans   RMQSessionDroppedException handling
 * 358344           280306 tpm      Comments for new exception handling
 * 520472           220508 cwilkin  Gathering reattaching
 * 496144           180608 cwilkin  Forward port PK58940
 * 558352           281008 cwilkin  Retry failed attach to avoid deadlock
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;

public interface ConsumerPoint
{
  
  /**
   * Returns the consumer manager for this consumer point
   * @return the consumer manager for this consumer point
   */
  public ConsumerManager getConsumerManager();
   
  /**
   * Closes the consumer session and notifies the exception listeners 
   * that the session has closed
   * @param e - may be null
   * @throws SIConnectionLostException
   * @throws SIResourceException
   * @throws SIErrorException
   */
  public void closeSession(Throwable e) throws SIConnectionLostException, SIResourceException, SIErrorException;
  
  /** Determines whether this consumer point is being used for gathering */
  public boolean isGatheringConsumer();
}
