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
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 186484.9         040504 tevans   Yet another runtime control point feature
 * 186484.10        170504 tevans   MBean Registration
 * 186484.14        160604 cwilkin  Virtual Link controllables
 * 190632.0.20      170604 caseyj   SIMPMessageProcessorControllable:resetMH()
 * 186484.16        220604 tevans   Xmit Queue runtime control
 * 190632.0.22      290604 caseyj   Implement MPControllable:resetLink()
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 186484.22.1      140704 cwilkin  Update javadoc
 * 219137           110804 ajw      Added SIMPException
 * 248030.1         240105 tpm      MBean extensions
 * SIB0105.mp.6     210607 nyoung   MQLink Controllable changes
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.processor.exceptions.*;
import com.ibm.ws.sib.processor.exceptions.SIMPException;

/**
 * The interface presented by a Message Procesor to perform dynamic
 * control operations.
 * <p>
 * The operations in this interface are specific to a Message Processor.
 */
public interface SIMPMessageProcessorControllable extends SIMPControllable
{
  /**
   * Locates the foreign buses known to this ME.
   *
   * @return An iterator over all of the ForeignBus objects.
   */
  public SIMPIterator getForeignBusIterator();

  /**
   * Locates the inter bus links known to this ME.
   *
   * @return An iterator over all of the InterBusLink objects.
   */
  public SIMPIterator getVirtualLinkIterator();

  /**
   * Locates the MQ links known to this ME.
   *
   * @return An iterator over all of the MQLink objects.
   */
  public SIMPIterator getMQLinkIterator();

  /**
   * Locates the local and remote queues known to and used by the MP
   * (including those which may be being deleted).
   *
   * @return An iterator over all of the SIMPQueueControllable objects.
   */
  public SIMPIterator getQueueIterator();

  /**
   * Locates the all the queues which are localized at this ME
   * (including those which may be being deleted).
   *
   * @return An iterator over all of the local SIMPQueueControllable objects.
   */
  public SIMPIterator getLocalQueueIterator();

  /**
   * Locates the all the queues which are localized at other MEs.
   * (including those which may be being deleted).
   *
   * @return An iterator over all of the remote SIMPQueueControllable objects.
   */
  public SIMPIterator getRemoteQueueIterator();

  /**
   * Locates a specific queue by it's ID
   * (including those which may be being deleted).
   *
   * @return A SIMPQueueControllable object.
   */
  public SIMPQueueControllable getQueueControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;

  /**
   * Locates a specific queue by it's Name and Bus
   * (NOT including those which are being deleted).
   *
   * @return A SIMPQueueControllable object.
   */
  public SIMPQueueControllable getQueueControlByName(String name, String bus)
    throws SIMPControllableNotFoundException,
           SIMPException;

  /**
   * Locates the topic spaces known to this MP.
   *
   * @return An iterator over all of the TopicSpace objects.
   */
  public SIMPIterator getTopicSpaceIterator();
  public SIMPTopicSpaceControllable getTopicSpaceControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;

  /**
   * Locates the local queues known to the MP and localized in this ME.
   *
   * @return An iterator over all of the SIMPLocalQueuePointControllable objects.
   */
  public SIMPIterator getLocalQueuePointIterator();
  public SIMPLocalQueuePointControllable getLocalQueuePointControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;

  /**
   * Locates the local subscriptions on this MP.
   *
   * @throws SIMPException if there is a corrupt subscription
   * @return An iterator over all of the SIMPLocalSubscriptionControllable objects.
   */
  public SIMPIterator getLocalSubscriptionIterator() throws SIMPException;
  public SIMPLocalSubscriptionControllable getLocalSubscriptionControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;

  /**
   * Locates the remote subscriptions on this MP.
   *
   * @return An iterator over all of the SIMPAttachedRemoteSubscriptionControllable objects.
   */
  public SIMPIterator getRemoteSubscriptionIterator();
//  public SIMPRemoteSubscriptionControllable getRemoteSubscriptionControlByID(String id)
//    throws SIMPInvalidRuntimeIDException,
//           SIMPControllableNotFoundException,
//           SIMPException;

  /**
   * Locates the remote queues known and in use by MP and localized in
   * remote MEs.
   *
   * @return An iterator over all of the SIMPRemoteQueuePointControllable objects.
   */
  public SIMPIterator getRemoteQueuePointIterator();
  public SIMPRemoteQueuePointControllable getRemoteQueuePointControlByID(String id)
    throws SIMPInvalidRuntimeIDException,
           SIMPControllableNotFoundException,
           SIMPException;

  /**
   * Locates the local and remote durable subscriptions known to the MP.
   *
   * @return An iterator over all of the DurableSubcription objects.
   */
  public SIMPIterator getKnownDurableSubscriptionsIterator();

  /**
   * Locates the core spi connections existing in the MP.
   *
   * @return An iterator over all of the Connection objects.
   */
  public SIMPIterator getConnectionsIterator();

  /**
   * Locates the alias destinations known to the MP.
   *
   * @return An iterator over all of the Alias objects.
   */
  public SIMPIterator getAliasIterator();

  /**
   * Locates the Foreign Destinations destinations known to the MP.
   *
   * @return An iterator over all of the ForeignDestination objects.
   */
  public SIMPIterator getForeignDestinationIterator();

  /**
   * Resets a corrupt destination such that on restart, it is deleted and
   * recreated.  This permanently deletes all the messages on that message
   * handler (they cannot be moved to the exception destination).
   * <p>
   * Calling reset on a message handler which is not corrupt will have no
   * effect.
   *
   * @param destName
   */
  public void resetDestination(String destName)
    throws SIMPRuntimeOperationFailedException;

  /**
   * Resets a corrupt link such that on restart, it is deleted and
   * recreated.  This permanently deletes all the messages on that link
   * (they cannot be moved to the exception destination).
   * <p>
   * Calling reset on a link which is not corrupt will have no effect.
   *
   * @param linkName
   */
  public void resetLink(String linkName)
    throws SIMPRuntimeOperationFailedException;

  /**
   * Returns a SIMPIterator of type SIMPLocalTopicSpaceControllable
   * @return
   * @author tpm
   */
  public SIMPIterator getLocalTopicSpaceControllables();

  /**
   * Returns a SIMPIterator of type SIMPRemoteTopicSpaceControllable
   * @return
   * @author tpm
   */
  public SIMPIterator getRemoteTopicSpaceControllables();
}
