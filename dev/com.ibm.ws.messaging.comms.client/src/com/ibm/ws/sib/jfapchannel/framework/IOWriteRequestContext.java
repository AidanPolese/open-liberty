/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/IOWriteRequestContext.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:25 [4/12/12 22:14:18]
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
 * 336594          060109 prestona JFAP channel for thin client
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.framework;

import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;

/**
 * Provides contextual information for a request to read data from the network.
 * Users of this package would typically obtain an implementation of this
 * interface by invoking the getReadInterface method from an implementation
 * of IOConnectionContext.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.IOConnectionContext
 */
public interface IOWriteRequestContext
{
   /**
    * Specifies the buffer from which data will be written.  This is semantically
    * equivalent to invoking setBuffers(new WsByteBuffer[]{buffer}).  This
    * method must only be invoked when no write request is in progress.
    * @param buffer the buffer to use for subsequent write requests.
    */
   void setBuffer(WsByteBuffer buffer);
   
   /**
    * Specifies a set of buffers from which data will be written.  This method must
    * only be invoked when no wrote request is in progress.
    * @param buffers a set of buffers to use for subsequent write requests.
    */
   void setBuffers(WsByteBuffer[] buffers);
   
   /** 
    * @return the buffer (or first buffer from the set of buffers) associated
    * with this write request.  This is semantically equivalent to invoking
    * getBuffers()[0].
    */
   WsByteBuffer getBuffer();
   
   /**
    * @return the set of buffers associated with this write request.
    */
   WsByteBuffer[] getBuffers();
   
   /**
    * Request to write data to the network.
    * @param amountToWrite the minimum amount of data to write before considering
    * that the request has been satisified.
    * @param completionCallback the callback to notify when the request completes
    * @param forceQueue must the write request be performed on another thread?  When
    * a value of true is specified then the write operation must not block the
    * thread invoking this method.  A value of false allows (but does not require)
    * the implementation to perform write operations using the calling thread.
    * @param timeout the number of milliseconds to wait for enough data to be written
    * such that the request is considered satisified.  A value of zero means 
    * "return immediately".  A timeout manifests itself as a call to the error method 
    * of the specified callback - passing a SocketTimeoutException.  Even when a timeout 
    * occures it is possible that some data will have been written. 
    * @return a network connection object if a subsequent write operation should be
    * attempted on the same thread - otherwise a value of null is returned.  This is
    * used as a mechanism to avoid the need for recursion if a value of true is
    * supplied to the forceQueue argument and the write request is being performed
    * on the calling thread.
    */
   NetworkConnection write(int amountToWrite, 
                           IOWriteCompletedCallback completionCallback, 
                           boolean queueRequest, 
                           int timeout);
   
   /**
    * Constant meaning "all data in the buffers" which may be specified as the
    * "amountToWrite" argument of the write method. 
    */
   final int WRITE_ALL_DATA = 0;
   
   /**
    * Constant meaning "wait forever" which may be specified as the "timeout"
    * argument of the write method.
    */
   final int NO_TIMEOUT = -1;
}
