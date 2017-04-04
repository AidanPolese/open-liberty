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
 * 186484.2         150304 tevans   Some intial controllable interfaces
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 199152           200404 gatfora  Correct javadoc.
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 201492           040504 gatfora  Move to undeprecated isOverrideOfQOSByProducerAllowed
 * 186484.10        170504 tevans   MBean Registration
 * 186484.16        220604 tevans   Xmit Queue runtime control
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 190632.0.24      050704 caseyj   Throw Exception for corruption on controls
 * 233498           210904 cwilkin  Remove isInQueisceMode
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import java.util.Map;

import com.ibm.websphere.sib.Reliability;
import com.ibm.ws.sib.processor.exceptions.*;
import com.ibm.ws.sib.processor.exceptions.SIMPException;
import com.ibm.wsspi.sib.core.DestinationType;

/**
 * The interface presented by a queue to perform dynamic
 * control operations.
 * <p>
 * The operations in this interface are specific to a queueing point.
 */
public interface SIMPQueueControllable extends SIMPMessageHandlerControllable
{
  /**
   * Get the SIMPLocalQueuePointControllable object
   * associated with this Queue.
   * 
   * @return A SIMPLocalQueuePointControllable object
   * 
   * @throws SIMPException if the queue is corrupt.
   */
  public SIMPLocalQueuePointControllable getLocalQueuePointControl()
    throws SIMPException;
  
  /**
   * Get an iterator over all of the SIMPRemoteQueuePointControllable objects
   * associated with this Queue.
   * 
   * @return An iterator containing SIMPRemoteQueuePointControllable objects.
   * 
   * @throws SIMPException if the queue is corrupt.
   */
  public SIMPIterator getRemoteQueuePointIterator() throws SIMPException;
  
  public SIMPRemoteQueuePointControllable getRemoteQueuePointControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;
           
  public SIMPRemoteQueuePointControllable getRemoteQueuePointControlByMEUuid(String meUuid)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;
               
  public long getAlterationTime();

  public DestinationType getDestinationType();  

  public Map getDestinationContext();

  public boolean isSendAllowed();
  public boolean isReceiveAllowed();
  public boolean isReceiveExclusive();
  

  public int getDefaultPriority();

  public String getExceptionDestination();

  public int getMaxFailedDeliveries();

  public boolean isOverrideOfQOSByProducerAllowed();

  public Reliability getDefaultReliability();
  public Reliability getMaxReliability();
}
