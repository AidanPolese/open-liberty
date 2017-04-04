/*
 * @start_prolog@
 * Version: @(#) 1.13 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ReceivedData.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:31:27 [4/12/12 22:14:11]
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel 
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * D289992         051114 prestona Reduce Semaphore creation
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel;

import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;

/**
 * An "unpacked" version of the data received back from an exchange call.
 * This is required so that the caller of exchange can recover information
 * like segment type from the call.
 * @author prestona
 */
public interface ReceivedData
{
   /**
    * The data received.
    * @return ByteBuffer
    */
   WsByteBuffer getBuffer();
   
   /**
    * The segment type of the data received.
    * @return int
    */
   int getSegmentType();
   
   /**
    * The request identifier of the data received.
    * @return int
    */
   int getRequestId();
   
   /**
    * The priority of the data received.
    * @return int
    */
   int getPriority();
   
   /**
    * Returns true iff the buffer being passed back as part of this
    * received data object was allocated from a buffer pool or not
    * @return boolean
    */
   boolean getAllocatedFromBufferPool();
   
   /**
    * Release the received data back to its underlying pool.
    */
   void release();
}
