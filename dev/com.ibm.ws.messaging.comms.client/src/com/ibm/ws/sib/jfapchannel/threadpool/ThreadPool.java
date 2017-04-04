/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/threadpool/ThreadPool.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:14 [4/12/12 22:14:17]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
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
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.threadpool;

/**
 * An interface which wrappers the thin and rich implementations of a thread pool.
 * 
 * @author Gareth Matthews
 */
public interface ThreadPool
{
   /**
    * Specifies that a dispatch should throw an exception if
    * the request queue is already full.
    *
    * @see com.ibm.ws.util.ThreadPool
    */
   public static final int ERROR_WHEN_QUEUE_IS_FULL = 1;
   
   /**
    * Initialises the thread pool wrapper with the specified name and sizes. This method must be
    * called before attempting to use the thread pool.
    * 
    * @param name The name of the thread pool.
    * @param minSize The minimum size of the thread pool.
    * @param maxSize The maximum size of the thread pool.
    */
   public void initialise(String name, int minSize, int maxSize);
   
   /**
    * Sets whether the thread pool is allowed to grow as needed.
    * 
    * @param growAsNeeded
    */
   public void setGrowAsNeeded(boolean growAsNeeded);

   /**
    * Executes the specified runnable using a thread from the pool.
    * 
    * @param runnable
    * @param was_thread_mode
    * 
    * @throws InterruptedException
    * @throws IllegalStateException
    * @throws ThreadPoolFullException
    */
   public void execute(Runnable runnable, int was_thread_mode) 
      throws InterruptedException, IllegalStateException, ThreadPoolFullException;

   /**
    * Executes the specified runnable using a thread from the pool.
    * 
    * @param runnable
    * 
    * @throws InterruptedException
    * @throws IllegalStateException
    */
   public void execute(Runnable runnable)
      throws InterruptedException, IllegalStateException;

   /**
    * Sets the keep-alive time.
    * 
    * @param msecs
    */
   public void setKeepAliveTime(long msecs);
   
   /**
    * Sets the size of the request buffer.
    * 
    * @param size
    */
   public void setRequestBufferSize(int size);
}
