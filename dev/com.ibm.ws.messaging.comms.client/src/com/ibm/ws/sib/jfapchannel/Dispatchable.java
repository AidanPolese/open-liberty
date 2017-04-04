/*
 * @start_prolog@
 * Version: @(#) 1.6 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/Dispatchable.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 09:54:44 [4/12/12 22:14:11]
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
 * Creation        040505 mattheg  Original
 * D199145         040812 prestona Fix Javadoc
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

/**
 * This interface should be implemented by classes who the JFap channel should dispatch work on.
 * For example, the JFap channel will dispatch data for the same conversation to the same 
 * dispatch queue (in most circumstances) and so the conversation should implement this interface.
 * <p>
 * The JFap channel will ask the conversation receive listener for a dispatchable object that the
 * data should be dispatched on. If the conversation receive listener returns null, the JFap channel
 * will queue data by conversation. 
 * 
 * @author Gareth Matthews
 */
public interface Dispatchable
{
   /**
    * This method should be called by JFap channel when it is allocating a new DispatchQueue for
    * this dispatchable object.
    * @param queue The queue to use.
    */
   public void setDispatchQueue(DispatchQueue queue);
   
   /**
    * Returns the current dispatch queue associated with this dispatchable object. If this returns
    * null, the JFap channel will allocate a new queue and associate it by calling the 
    * setDispatchQueue method.
    * @return Returns the dispatch queue or null if one has been associated.
    */
   public DispatchQueue getDispatchQueue();
   
   /**
    * @return Returns an object that should be synchronized on when modifying the reference count
    *         and queue.
    */
   public Object getDispatchLockObject();
   
   /**
    * Since a dispatch queue can hold multiple items of data from the same dispatcable, as well as
    * multiple items of data for other dispatchables, a dispatchable should maintain a reference 
    * count. This way, when the reference count is zero the queue can be dis-associated with this
    * dispatchable.
    * <p>
    * This method increments the use count and should be called when an item of data is added to
    * the associated queue.
    */
   public void incrementDispatchQueueRefCount();
   
   /**
    * Since a dispatch queue can hold multiple items of data from the same dispatcable, as well as
    * multiple items of data for other dispatchables, a dispatchable should maintain a reference 
    * count. This way, when the reference count is zero the queue can be dis-associated with this
    * dispatchable.
    * <p>
    * This method decrements the use count and should be called when an item of data is removed from
    * the associated queue.
    */   
   public void decrementDispatchQueueRefCount();
   
   /**
    * This method returns the reference count for this dispatchable. A reference count of zero 
    * indicates that it is safe to disassociate the dispatch queue with this dispatchable.
    * @return Returns the current reference count of this dispatchable on the dispatch queue.
    */
   public int getDispatchQueueRefCount();
}
