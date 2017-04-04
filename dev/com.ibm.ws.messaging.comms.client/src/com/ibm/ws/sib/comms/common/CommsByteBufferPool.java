/*
 * @start_prolog@
 * Version: @(#) 1.4 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/common/CommsByteBufferPool.java, SIB.comms, WASX.SIB, uu1215.01 06/09/04 03:21:54 [4/12/12 22:14:09]
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
 * Creation        060717 mattheg  Use CommsByteBuffer
 * SIB0048b.com.1  060901 mattheg  Allow class to be overriden
 * ============================================================================
 */
package com.ibm.ws.sib.comms.common;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.util.ObjectPool;

/**
 * The comms byte buffer pool.
 * 
 * @author Gareth Matthews
 */
public class CommsByteBufferPool
{
   /** Trace */
   private static final TraceComponent tc = SibTr.register(CommsByteBufferPool.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);
   
   /** The singleton instance of this class */
   private static CommsByteBufferPool instance = null;
   
   /**
    * @return Returns the byte buffer pool.
    */
   public static synchronized CommsByteBufferPool getInstance()
   {
      if (instance == null)
      {
         instance = new CommsByteBufferPool();
      }
      return instance;
   }

   
   /** Our object pool */
   private ObjectPool pool = null;

   /**
    * Constructs the initial pool.
    */
   protected CommsByteBufferPool()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>");
      pool = new ObjectPool(getPoolName(), 100);
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   /**
    * Gets a CommsByteBuffer from the pool.
    * 
    * @return CommsString
    */
   public synchronized CommsByteBuffer allocate()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "allocate");
   
      // Remove a CommsByteBuffer from the pool
      CommsByteBuffer buff = (CommsByteBuffer) pool.remove();
   
      // If the buffer is null then there was none available in the pool
      // So create a new one
      if (buff == null)
      {
         if (tc.isDebugEnabled()) SibTr.debug(this, tc, "No buffer available from pool - creating a new one");
      
         buff = createNew();
      }
   
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "allocate", buff);
      return buff;
   }
   
   /**
    * Creates a new buffer. This method may be overriden to create a new type of CommsByteBuffer.
    * 
    * @return Returns the new buffer.
    */
   protected CommsByteBuffer createNew()
   {
      return new CommsByteBuffer(this);
   }
   
   /**
    * @return Returns the name for the pool.
    */
   protected String getPoolName()
   {
      return "CommsByteBufferPool";
   }

   /**
    * Returns a buffer back to the pool so that it can be re-used.
    * 
    * @param buff
    */
   synchronized void release(CommsByteBuffer buff)
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "release", buff);
      buff.reset();
      pool.add(buff);
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "release");
   }
}
