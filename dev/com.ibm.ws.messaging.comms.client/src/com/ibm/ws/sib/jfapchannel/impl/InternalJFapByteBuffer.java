/*
 * @start_prolog@
 * Version: @(#) 1.3 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/InternalJFapByteBuffer.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:07:42 [4/12/12 22:14:13]
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
 * Creation        060725 mattheg  Use of JFapByteBuffer
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.impl;

import com.ibm.ws.sib.jfapchannel.JFapByteBuffer;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;

/**
 * This class is designed to be used by the JFap channel internally when it needs to send data.
 * 
 * @author Gareth Matthews
 */
public class InternalJFapByteBuffer extends JFapByteBuffer
{
   /**
    * Used for constructing a buffer that has no initial payload. This data can simply be sent as
    * it is, for added to using the putXXX() methods.
    */
   public InternalJFapByteBuffer()
   {
      super();
   }
   
   /**
    * Used for constructing ping responses from a ping request. The buffer is preloaded with the
    * data passed in. Note that the data is not copied from the buffer. Once the data is sent the
    * buffer will be automatically released. This buffer is read-only when using this constructor.
    * 
    * @param buffer
    */
   public InternalJFapByteBuffer(WsByteBuffer buffer)
   {
      reset();
      receivedBuffer = buffer;
   }
}
