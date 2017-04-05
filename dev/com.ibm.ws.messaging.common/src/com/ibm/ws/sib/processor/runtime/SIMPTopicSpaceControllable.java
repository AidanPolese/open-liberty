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
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.7         290404 tevans   More runtime control interfaces and implementation
 * 186484.9         040504 tevans   Yet another runtime control point feature
 * 186484.10        170504 tevans   MBean Registration
 * 186484.16        220604 tevans   Xmit Queue runtime control
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 190632.0.24      050704 caseyj   Throw Exception for corruption on controls
 * 186484.22        090704 cwilkin  add getDurableSubscriptionIterator
 * 219137           110804 ajw      Added SIMPException
 * 229095.1         060904 gatfora  Provide a deleteSubscription method.
 * 339586.2         020206 tpm      getLocalSubscriptionControlByName 
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.exceptions.SIMPControllableNotFoundException;
import com.ibm.ws.sib.processor.exceptions.SIMPException;
import com.ibm.ws.sib.processor.exceptions.SIMPIncorrectCallException;
import com.ibm.ws.sib.processor.exceptions.SIMPInvalidRuntimeIDException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionNotFoundException;
import com.ibm.wsspi.sib.core.exception.SIDestinationLockedException;

/**
 * The interface presented by a topicspace to perform dynamic
 * control operations.
 */
public interface SIMPTopicSpaceControllable extends SIMPMessageHandlerControllable
{
  /**
   * Get the remote durable subscriptions that consumers in this ME are attached to. 
   *
   * @return An iterator over all of the AttachedRemoteSubscriber summary objects. 
   */
  public SIMPIterator getAttachedRemoteSubscriberIterator() 
    throws SIMPException;

  /**
   * Get the local durable and non durable subscriptionsin this ME 
   * whether they have consumers attached to them or not. 
   *
   * @throws SIMPException if there is a corrupt subscription
   * @return An iterator over all of the LocalSubscription summary objects. 
   */
  public SIMPIterator getLocalSubscriptionIterator() throws SIMPException;
  
  public SIMPLocalSubscriptionControllable getLocalSubscriptionControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;
  
  
  /**
   * Returns a SIMPLocalSubscriptionControllable with the specified name.
   * NOTE: this is not to be confused with a subscription ID.
   * The subscription name only applies to Durable subscriptions.
   * Therefore this method will always return either a SIMPLocalSubscriptionControllable
   * for a durable subscription or will throw a SIMPControllableNotFoundException.
   * @param subscriptionName
   * @return a SIMPLocalSubscriptionControllable for the subcription.
   */
  public SIMPLocalSubscriptionControllable getLocalSubscriptionControlByName(String subscriptionName) 
    throws  SIMPException,
            SIMPControllableNotFoundException;
  
  /** Deletes a local durable subscription.
   * 
   * In the case where the subscription no longer exists a SIMPControllableNotFoundException
   * exception is thrown.
   * When the subscription isn't a durable one a SIMPIncorrectCallException exception is thrown   
   * 
   * @param id  The subscription controlable id to delete 
   */         
  public void deleteLocalSubscriptionControlByID(String id)
  throws SIMPInvalidRuntimeIDException, 
         SIMPControllableNotFoundException, 
         SIMPIncorrectCallException,
         SIMPException,
         SIDurableSubscriptionNotFoundException, 
         SIDestinationLockedException, 
         SIResourceException, 
         SIIncorrectCallException;
 

  /**
   * Get the local topic spaces in this ME. 
   *
   * @return An iterator over the LocalTopicSpace summary objects. 
   */
  public SIMPLocalTopicSpaceControllable getLocalTopicSpaceControl();

  /**
   * Locates the remote topic spaces in this ME.
   * Each remote topic space representa a child ME in the Publish Subscribe hierarchy. 
   *
   * @return An iterator over RemoteTopicSpace summary objects. 
   */
  public SIMPIterator getRemoteTopicSpaceIterator();
  public SIMPRemoteTopicSpaceControllable getRemoteTopicSpaceControlByID(String id) 
  throws SIMPControllableNotFoundException;
  
}
