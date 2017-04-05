/*
 * @start_prolog@
 * Version: @(#) 1.2 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/ExchangeReceiveListenerPool.java, SIB.comms, WASX.SIB, uu1215.01 05/11/15 02:40:30 [4/12/12 22:14:14]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) Copyright IBM Corp. 2005 
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
 * D289992         051114 prestona Reduce Semaphore creation
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.impl;

/**
 * Trivial pool for exchange receive listeners
 */
public class ExchangeReceiveListenerPool
{
   private final int size;
   private final ExchangeReceiveListener[] pool;
   private int entries = 0;
   
   public ExchangeReceiveListenerPool(int size)
   {
      this.size = size;
      pool = new ExchangeReceiveListener[size];
   }
   
   public synchronized ExchangeReceiveListener allocate(int expectedRequestNumber)
   {
      final ExchangeReceiveListener result;
      
      if (entries == 0)
      {
         result = new ExchangeReceiveListener(this);
      }
      else
      {
         --entries;         
         result = pool[entries];
         pool[entries] = null;
      }
      result.setExpectedRequestNumber(expectedRequestNumber);
      
      return result;
   }
   
   protected synchronized void release(ExchangeReceiveListener listener)
   {
      listener.reset();
      if (entries < size)
      {
         pool[entries] = listener;
         ++entries;
      }
   }
}
