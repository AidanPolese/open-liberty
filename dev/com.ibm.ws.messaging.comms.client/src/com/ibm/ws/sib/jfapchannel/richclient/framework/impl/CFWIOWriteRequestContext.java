/*
 * @start_prolog@
 * Version: @(#) 1.5 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/CFWIOWriteRequestContext.java, SIB.comms, WASX.SIB, uu1215.01 08/06/26 02:44:27 [4/12/12 22:14:19]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2006, 2008
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
 * D504181         080312 mleming  Don't NPE if passed in null buffer or buffers
 * 505216          080314 sibcopyr Automatic update of trace guards
 * 522407          080521 djvines  Use autoboxing for trace
 * 532083          080625 mleming  NPE in getBuffer/getBuffers
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.framework.impl;

import java.io.IOException;
import java.util.Arrays;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBufferPool;
import com.ibm.ws.sib.jfapchannel.framework.IOWriteCompletedCallback;
import com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnection;
import com.ibm.ws.sib.jfapchannel.richclient.buffer.impl.RichByteBufferPool;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPWriteCompletedCallback;
import com.ibm.wsspi.tcpchannel.TCPWriteRequestContext;

/**
 * An implementation of com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext. It
 * basically wrappers the com.ibm.wsspi.tcp.channel.TCPWriteRequestContext code in the
 * underlying TCP channel.
 *
 * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext
 * @see com.ibm.wsspi.tcp.channel.TCPWriteRequestContext
 *
 * @author Gareth Matthews
 */
public class CFWIOWriteRequestContext extends CFWIOBaseContext implements IOWriteRequestContext
{
   /** Trace */
   private static final TraceComponent tc = SibTr.register(CFWIOWriteRequestContext.class,
                                                           JFapChannelConstants.MSG_GROUP,
                                                           JFapChannelConstants.MSG_BUNDLE);

   /** Log class info on load */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/CFWIOWriteRequestContext.java, SIB.comms, WASX.SIB, uu1215.01 1.5");
   }

   /** The actual TCP channel write context */
   private TCPWriteRequestContext writeCtx = null;

   /**
    * @param writeCtx
    */
   public CFWIOWriteRequestContext(NetworkConnection conn, TCPWriteRequestContext writeCtx)
   {
      super(conn);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", new Object[] { conn, writeCtx });
      this.writeCtx = writeCtx;
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "<init>");
   }

   /**
    *
    * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext#setBuffer(com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer)
    */
   public void setBuffer(WsByteBuffer buffer)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setBuffer", buffer);

      if(buffer != null)
      {
         writeCtx.setBuffer((com.ibm.wsspi.bytebuffer.WsByteBuffer) buffer.getUnderlyingBuffer());
      }
      else
      {
         writeCtx.setBuffer(null);
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setBuffer");
   }

   /**
    *
    * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext#setBuffers(com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer[])
    */
   public void setBuffers(WsByteBuffer[] buffers)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setBuffers", Arrays.toString(buffers));

      if(buffers != null)
      {
         com.ibm.wsspi.bytebuffer.WsByteBuffer[] tcpBuffers = new com.ibm.wsspi.bytebuffer.WsByteBuffer[buffers.length];
         for (int x = 0; x < buffers.length; x++)
         {
            tcpBuffers[x] = (com.ibm.wsspi.bytebuffer.WsByteBuffer) buffers[x].getUnderlyingBuffer();
         }
         writeCtx.setBuffers(tcpBuffers);
      }
      else
      {
         writeCtx.setBuffer(null);
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setBuffers");
   }

   /**
    *
    * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext#getBuffer()
    */
   public WsByteBuffer getBuffer()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getBuffer");
      WsByteBuffer jfapByteBuffer = ((RichByteBufferPool) WsByteBufferPool.getInstance()).wrap(writeCtx.getBuffer());
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getBuffer", jfapByteBuffer);
      return jfapByteBuffer;
   }

   /**
    * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext#getBuffers()
    */
   public WsByteBuffer[] getBuffers()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getBuffers");

      final com.ibm.wsspi.bytebuffer.WsByteBuffer[] tcpBuffers = writeCtx.getBuffers();
      WsByteBuffer[] jfapByteBuffers = null;

      if(tcpBuffers != null) 
      {
        jfapByteBuffers = new WsByteBuffer[tcpBuffers.length];

        int nonNullCount = 0;

        for(com.ibm.wsspi.bytebuffer.WsByteBuffer buffer: tcpBuffers)
        {
           //Only wrap non-null buffers
           if(buffer != null)
           {
              jfapByteBuffers[nonNullCount++] = ((RichByteBufferPool) WsByteBufferPool.getInstance()).wrap(buffer);
           }
        }

        //tcpBuffers contained null entries, compact it
        if(nonNullCount < tcpBuffers.length)
        {
           final WsByteBuffer[] compactedArray = new WsByteBuffer[nonNullCount];
           System.arraycopy(jfapByteBuffers, 0, compactedArray, 0, nonNullCount);
           jfapByteBuffers = compactedArray;
        }
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getBuffers", Arrays.toString(jfapByteBuffers));
      return jfapByteBuffers;
   }

   /**
    *
    * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext#write(int, com.ibm.ws.sib.jfapchannel.framework.IOWriteCompletedCallback, boolean, int)
    */
   public NetworkConnection write(int amountToWrite, final IOWriteCompletedCallback completionCallback,
                                  boolean queueRequest, int timeout)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "write",
                                           new Object[]{amountToWrite, completionCallback, queueRequest, timeout});

      NetworkConnection retConn = null;
      final IOWriteRequestContext me = this;

      // Create a real TCP callback to use with this operation
      TCPWriteCompletedCallback callback = new TCPWriteCompletedCallback() {
         /**
          * Called when the write operation completes.
          *
          * @see com.ibm.wsspi.tcp.channel.TCPWriteCompletedCallback#complete(com.ibm.websphere.channelfw.VirtualConnection, com.ibm.wsspi.tcp.channel.TCPWriteRequestContext)
          */
         public void complete(VirtualConnection vc, TCPWriteRequestContext ctx)
         {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "complete", new Object[]{vc, ctx});

            completionCallback.complete(getNetworkConnectionInstance(vc), me);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "complete");
         }

         public void error(VirtualConnection vc, TCPWriteRequestContext ctx, IOException ioException)
         {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "error", new Object[]{vc, ctx, ioException});

            completionCallback.error(getNetworkConnectionInstance(vc),
                                     me,
                                     ioException);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "error");
         }
      };

      // Do the write
      long dataToWrite = amountToWrite;
      if (amountToWrite == WRITE_ALL_DATA) dataToWrite = TCPWriteRequestContext.WRITE_ALL_DATA;

      VirtualConnection vc = writeCtx.write(dataToWrite,
                                            callback,
                                            queueRequest,
                                            timeout);

      if (vc != null)
      {
         retConn = getNetworkConnectionInstance(vc);
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "write", retConn);
      return retConn;
   }

}
