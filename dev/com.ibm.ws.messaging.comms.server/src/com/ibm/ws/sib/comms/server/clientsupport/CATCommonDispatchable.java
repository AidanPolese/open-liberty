/*
 * @start_prolog@
 * Version: @(#) 1.7 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATCommonDispatchable.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 07:56:35 [7/2/12 05:58:59]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2004, 2005
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        040506 mattheg  Original
 * D199177         040816 mattheg  JavaDoc
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.ws.sib.jfapchannel.DispatchQueue;
import com.ibm.ws.sib.jfapchannel.Dispatchable;

/**
 * In the Comms layer we usually want the JFap channel to dispatch work to our receive listener
 * by Conversation - ie so that all data for a single conversation is executed serially. 
 * However, to increase throughput, we can allow the JFap channel to dispatch work more efficiently
 * than that. For example, work done under a transaction could be done on a different thread to that
 * of non-transacted work, regardless of the Conversation.
 * <p>
 * This class provides the base functionality in allowing an object to be dispatchable. It should be
 * used by for example the CATTransaction class, which can then extend this class and be returned
 * when the JFap channel calls the getThreadContext method on the receive listener.
 * 
 * @author Gareth Matthews
 */
public abstract class CATCommonDispatchable implements Dispatchable
{
   /** The dispatch queue this object is using */
   private DispatchQueue dispatchQueue = null;

   /** The lock object */
   private Object lock = new Object();
   
   /** The dispatch queue reference count */
   private int refCount = 0;

   /**
    * Sets the dispatch queue for this object.
    * 
    * @param queue
    */
   public void setDispatchQueue(DispatchQueue queue)
   {
      this.dispatchQueue = queue;
   }

   /**
    * @return Returns the dispatch queue for this object.
    */
   public DispatchQueue getDispatchQueue()
   {
      return dispatchQueue;
   }

   /**
    * @return Returns an object that can be synchronized on before doing anything with this object.
    */
   public Object getDispatchLockObject()
   {
      return lock;
   }

   /**
    * Increments the dispatch queue ref count.
    */
   public void incrementDispatchQueueRefCount()
   {
      ++refCount;
   }

   /**
    * Decrements the dispatch queue ref count.
    */
   public void decrementDispatchQueueRefCount()
   {
      --refCount;
   }

   /**
    * @return Returns the dispatch queue ref count.
    */
   public int getDispatchQueueRefCount()
   {
      return refCount;
   }

}
