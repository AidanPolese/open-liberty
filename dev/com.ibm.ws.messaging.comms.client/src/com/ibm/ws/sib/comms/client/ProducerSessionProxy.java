/*
 * @start_prolog@
 * Version: @(#) 1.80 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/ProducerSessionProxy.java, SIB.comms, WASX.SIB, uu1215.01 11/12/16 12:21:53 [4/12/12 22:14:05]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2011 
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
 * Creation        030325 niall    Original
 * F166959         030521 niall    Rebase on non-prototype CF + TCP Channel
 * F166631.02      030522 prestona Add MessageEncode exception handler 
 * D168072         030602 prestona Use get/setReliability
 * d168211         030605 mattheg  Allow checking of closed sessions / connections
 * d170527         030625 mattheg  Tidy and change to SibTr
 * d170639         030627 mattheg  NLS all the messages
 * f168604.2       030704 prestona Exploit asynchronous API from client
 * d169897.2       030707 schmittm Provide remote client implementation of new Core API as defined
 * f171400         030710 schmittm Implement Core API 0.6 changes in client and server code
 * d173559         030805 mattheg  Ensure an express send uses sendNoReply
 * F174602         030819 prestona Switch to using SICommsException
 * f174317         030827 mattheg  Add local transaction support
 * f173765.2       030925 mattheg  Core API M4 update
 * f177889         030929 mattheg  Core API M4 completion
 * f181927         031111 mattheg  Allow global transaction support to be exposed
 * f179339.4       031222 mattheg  Forward and reverse routing support
 * d181719         040108 mattheg  Transaction synchronisation support
 * d186970         040116 mattheg  Overhaul the way we send exceptions
 * f184390.3       040123 mattheg  Revised Qos
 * f187521.2.1     040127 mattheg  Use constants rather than hardcoded values
 * f188585         040128 mattheg  MFP Schema propogation -- part 2
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * d189716         040218 mattheg  FFDC Instrumentation
 * d187252         040302 mattheg  Ensure session destination information is only returned if it changes
 * f192759.2       040311 mattheg  Use correct length for message size
 * f192829         040407 mattheg  Client tuning parameters
 * F195720.3       040616 prestona WAS Request Metrics in Jetstream
 * F195720.3.1     040629 prestona WAS Request Metrics in Jetstream
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * D225797         040820 mattheg  Fix up possible NPE when connection is lost on exchange
 * D235891         040930 mattheg  Runtime property standards
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D241156         041026 mattheg  Make use of order context use counts
 * F247975         050204 prestona Use altertnative messaging encoding routine
 * D262663         050406 prestona Deadlock running jetstream cases EC3 413004
 * SIB0025.comm    050903 prestona PMI RM improvements
 * D307265         050918 prestona Support for optimized transactions
 * D315210         241005 ajw      Add reqScope param to send/recieve calls
 * D318108         051031 prestona Fix problem where low QOS messages get exchanged rather than sent.
 * D320185         051104 mattheg  send() doesn't throw the right exceptions
 * D272899         051109 mattheg  Ensure we check if conversation is closed before calling encode()
 * D289992         051114 prestona Reduce Semaphore creation
 * D346111         060404 mattheg  Fix defect 272899 properly
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * D348294.2       060921 mattheg  putMessage() method signature update
 * SIB0112c.com.1  070125 mattheg  Memory management: Parse message in chunks
 * D434395         070424 prestona FINBUGS: fix findbug warnings in sib.comms.client.impl
 * 504113          080404 mleming  Only get constants once
 * 511071          080408 sibcopyr Automatic update of trace guards
 * 538413          080725 djvines  Trace improvements to follow message flow
 * PM42438         280611 ajw      Use Reentrant write/read lock rather than a non reentrant write/read lock
 * F1344-55985     161211 skavitha Add records if XCT enabled
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsLightTrace;
import com.ibm.ws.sib.comms.common.CommsUtils;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.HandshakeProperties;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.utils.DataSlice;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.DestinationType;
import com.ibm.wsspi.sib.core.ProducerSession;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 * This class acts as a Core API ProducerSession object and will
 * proxy all the calls to a remote messaging engine where the actual
 * calls will be performed.
 * <p>
 * A producer session takes care of producing messages, or sending them
 * to a destination. Messages are sent at the JFAP priority that corresponds
 * to their actual SIBusMessage priority. Care is taken to ensure that
 * a close() flow is sent at a lower priority than any actual messages.
 */
public class ProducerSessionProxy extends DestinationSessionProxy implements ProducerSession 
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = ProducerSessionProxy.class.getName();
   
   /** Register Class with Trace Component */
   private static final TraceComponent tc = SibTr.register(ProducerSessionProxy.class, 
                                                           CommsConstants.MSG_GROUP, 
                                                           CommsConstants.MSG_BUNDLE);
   
   /** The NLS reference */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);
      
   private ReentrantReadWriteLock closeLock;
   
   /** The lowest priority message that has been sent on this session */
   private short lowestPriority = JFapChannelConstants.PRIORITY_HIGHEST;
   
   /** Whether we should exchange transacted sends */
   private static final boolean exchangeTransactedSends =  
      CommsUtils.getRuntimeBooleanProperty(CommsConstants.EXCHANGE_TX_SEND_KEY,
                                           CommsConstants.EXCHANGE_TX_SEND);
                                          
   /** Whether we should exchange express sends */
   private static final boolean exchangeExpressSends = 
      CommsUtils.getRuntimeBooleanProperty(CommsConstants.EXCHANGE_EXPRESS_END_KEY, 
                                           CommsConstants.EXCHANGE_EXPRESS_SEND);


   /** The ordering context associated with this producer */
   private OrderingContextProxy oc = null;
   
   /** The destination type for this producer */
   private final DestinationType destType;
   
   /** Log Source code level on static load of class */
   static 
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/ProducerSessionProxy.java, SIB.comms, WASX.SIB, uu1215.01 1.80");
   }
   
   /**
    * Constructor
    * 
    * @param con
    * @param cp
    * @param oc
    * @param buf
    * @param destAddr
    * @param destType The destination type the user gave when creating the session.
    */
   public ProducerSessionProxy(Conversation con, 
                               ConnectionProxy cp, 
                               OrderingContextProxy oc,
                               CommsByteBuffer buf, 
                               SIDestinationAddress destAddr,
                               DestinationType destType)
   {
      super(con, cp);
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>",
                                           new Object[] {con, cp, oc, buf, destAddr, destType});
      
      this.oc = oc;		
      this.destType = destType;
      setDestinationAddress(destAddr);
      inflateData(buf);
      closeLock = cp.closeLock;
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }
      
   /**
    * Proxies the identically named method on the server.
    * Sends the msg to the Destination specified in the createProducerSessionCall. 
    * Optionally, a transaction may be supplied. Optionally, a QualityOfService may 
    * be supplied, which must be no stronger than that of the Destination (otherwise
    * an exception is thrown). If a QualityOfService is supplied then the Mesasge 
    * Processor will implement delivery semantics no weaker than those of the send 
    * call and no stronger than those of the Destination. 
    * 
    * @param msg
    * @param tran
    * 
    * @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
    * @throws com.ibm.websphere.sib.exception.SIResourceException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
    * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
    * @throws com.ibm.websphere.sib.exception.SIErrorException
    * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
    * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
    * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
    */
   public void send(SIBusMessage msg, SITransaction tran) 
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, SILimitExceededException, 
             SIErrorException,
             SINotAuthorizedException,
             SIIncorrectCallException,
             SINotPossibleInCurrentConfigurationException 
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "send", new Object[] {msg, tran});
      
      try
      {
         closeLock.readLock().lockInterruptibly();
         
         try
         {            
            checkAlreadyClosed();
            
            // XCT Instrumentation for SIBus
            //lohith liberty change
           /* if(XctSettings.isAnyEnabled())
            {
               Xct xct = Xct.current();
               if(xct.annotationsEnabled())
               {
                  Annotation annotation= new Annotation(XctJmsConstants.XCT_SIBUS).add(XctJmsConstants.XCT_PROXY_SEND); 
                  annotation.associate(XctJmsConstants.XCT_DEST_NAME,getDestinationAddress().getDestinationName());
                  annotation.add(new Annotation(XctJmsConstants.XCT_DEST_TYPE).add(destType.toString()));
                  String transacted = XctJmsConstants.XCT_TRANSACTED_FALSE;
                  if (tran !=null)
                     transacted = XctJmsConstants.XCT_TRANSACTED_TRUE;
                  annotation.add(new Annotation(XctJmsConstants.XCT_TRANSACTED).add(transacted));
                  annotation.add(new Annotation(XctJmsConstants.XCT_RELIABILITY).add(msg.getReliability().toString()));
                
                  xct.begin(annotation);
               }
               else
                  xct.begin();
                   
               String xctCorrelationIDString = Xct.current().toString();
               msg.setXctCorrelationID(xctCorrelationIDString);
            }*/
            // Now we need to synchronise on the transaction object if there is one.
            if (tran != null)
            {
               synchronized (tran)
               {
                  // Check transaction is in a valid state.
                  // Enlisted for an XA UOW and not rolledback or
                  // completed for a local transaction.
                  if (!((Transaction) tran).isValid())
                  {
                     throw new SIIncorrectCallException(
                        nls.getFormattedMessage("TRANSACTION_COMPLETE_SICO1022", null, null)
                     );
                  }
                        
                  _send(msg, tran);
               }
            }
            else
            {
               _send(msg, null);
            }
            
            //lohith liberty change
            /*if(XctSettings.isAnyEnabled())
            {
               Xct xct = Xct.current();
               if(xct.annotationsEnabled())
               {
                  Annotation annotation= new Annotation(XctJmsConstants.XCT_SIBUS).add(XctJmsConstants.XCT_PROXY_SEND);                          
                  xct.end(annotation);          
               }
               else 
                  xct.end();            
            }*/
         }
         finally
         {
            closeLock.readLock().unlock();
         }
      }
      catch (InterruptedException e)
      {
         // No FFDC code needed
      }
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "send");
   }

   /**
    * This method performs the actual send, but does no parameter checking
    * and gets no locks needed to perform the send. This should be done
    * by a suitable 'super'-method.
    *
    * @param msg
    * @param tran
    * 
    * @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
    * @throws com.ibm.websphere.sib.exception.SIResourceException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
    * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
    * @throws com.ibm.websphere.sib.exception.SIErrorException
    * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
    * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
    * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException  
    */
   private void _send(SIBusMessage msg, SITransaction tran)
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, SILimitExceededException, 
             SIErrorException,
             SINotAuthorizedException,
             SIIncorrectCallException,
             SINotPossibleInCurrentConfigurationException 
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "_send");
      
      boolean sendSuccessful = false;
      // Get the message priority
      short jfapPriority = JFapChannelConstants.getJFAPPriority(msg.getPriority());
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Sending with JFAP priority of " + jfapPriority);
            
      updateLowestPriority(jfapPriority);

      // *** Complex logic to determine if we can get away we need a reply to ***
      // *** this send operation.                                             ***
      final boolean requireReply;
      if (tran != null && !exchangeTransactedSends)
      {
         // If there is a transaction, and we haven't been explicitly told to exchange
         // transacted sends - then a reply is NOT required.
         requireReply = false;
      }
      else if (exchangeExpressSends)
      {
         // We have been prohibited from sending (rather than exchanging) low
         // qualities of service - thus there is no way that we can avoid requiring
         // a reply.
         requireReply = true;
      }
      else
      {
         // We CAN perform the optimization where low qualities of service can be sent
         // without requiring a reply.  Check the message quality of service.
         requireReply = (msg.getReliability() != Reliability.BEST_EFFORT_NONPERSISTENT) &&
                        (msg.getReliability() != Reliability.EXPRESS_NONPERSISTENT);
      }
      // *** end of "is a reply required" logic ***
      
      // If we are at FAP9 or above we can do a 'chunked' send of the message in seperate 
      // slices to make life easier on the Java memory manager
      final HandshakeProperties props = getConversation().getHandshakeProperties(); 
      if (props.getFapLevel() >= JFapChannelConstants.FAP_VERSION_9)
      {
         sendChunkedMessage(tran, msg, requireReply, jfapPriority);
      }
      else
      {
         sendEntireMessage(tran, msg, null, requireReply, jfapPriority);
      }
      
      sendSuccessful = true;

      if (TraceComponent.isAnyTracingEnabled()) 
        CommsLightTrace.traceMessageId(tc, "SendMsgTrace", msg);
      
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "_send");
   }

   /**
    * Sends the entire message in one big JFap message. This requires the allocation of one big
    * area of storage for the whole message. If the messageSlices parameter is not null then the 
    * message has already been encoded and can just be added into the buffer without encoding again.
    * 
    * @param tran
    * @param msg
    * @param messageSlices
    * @param requireReply
    * @param jfapPriority
    * 
    * @throws SIResourceException
    * @throws SISessionUnavailableException
    * @throws SINotPossibleInCurrentConfigurationException
    * @throws SIIncorrectCallException
    * @throws SIConnectionUnavailableException
    */
   private void sendEntireMessage(SITransaction tran, SIBusMessage msg, 
                                  List<DataSlice> messageSlices, boolean requireReply, 
                                  short jfapPriority) 
      throws SIResourceException, SISessionUnavailableException, 
             SINotPossibleInCurrentConfigurationException, SIIncorrectCallException, 
             SIConnectionUnavailableException
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "sendEntireMessage", 
                                           new Object[]{tran, msg, messageSlices, requireReply, jfapPriority});
      
      CommsByteBuffer request = getCommsByteBuffer();
      request.putShort(getConnectionObjectID());
      request.putShort(getProxyID());
      request.putSITransaction(tran);
      
      if (messageSlices == null)
      {
         request.putClientMessage(msg, getCommsConnection(), getConversation());
      }
      else
      {
         request.putMessgeWithoutEncode(messageSlices);
      }
      
      sendData(request, 
               jfapPriority, 
               requireReply, 
               tran,
               JFapChannelConstants.SEG_SEND_SESS_MSG,
               JFapChannelConstants.SEG_SEND_SESS_MSG_NOREPLY,
               JFapChannelConstants.SEG_SEND_SESS_MSG_R);
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "sendEntireMessage");
   }

   /**
    * This method will send a message but in chunks. The chunks that are sent are the exact chunks
    * that are given to us by MFP when we encode the message for the wire. These chunks are actually
    * sent in seperate transmissions.
    * 
    * @param tran
    * @param msg
    * @param requireReply
    * @param jfapPriority
    * 
    * @throws SIResourceException
    * @throws SISessionUnavailableException
    * @throws SINotPossibleInCurrentConfigurationException
    * @throws SIIncorrectCallException
    * @throws SIConnectionUnavailableException
    */
   private void sendChunkedMessage(SITransaction tran, SIBusMessage msg, boolean requireReply, 
                                   short jfapPriority)
      throws SIResourceException, SISessionUnavailableException, 
             SINotPossibleInCurrentConfigurationException, SIIncorrectCallException, 
             SIConnectionUnavailableException
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "sendChunkedMessage", 
                                           new Object[]{tran, msg, requireReply, jfapPriority});
      
      CommsByteBuffer request = getCommsByteBuffer();
      List<DataSlice> messageSlices = null;
      
      // First job is to encode the message in data slices
      try
      {
         messageSlices = request.encodeFast((JsMessage) msg, getCommsConnection(), getConversation());
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Message encoded into " + messageSlices.size() + " slice(s)");
      }
      catch (SIConnectionDroppedException e)
      {
         // No FFDC Code Needed
         // Simply pass this exception on
         throw e;
      }
      catch (Exception e)
      {
         FFDCFilter.processException(e, CLASS_NAME + ".sendChunkedMessage",
                                     CommsConstants.PRODUCERSESSIONPROXY_SENDCHUNKED_01,
                                     new Object[] { msg, this });
         
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Unable to encode message", e);
         throw new SIResourceException(e);
      }
      
      // Do a quick check on the message size. If the size is less than our threshold for chunking
      // the message then send it as one.
      int msgLen = 0;
      for (DataSlice slice : messageSlices)  msgLen += slice.getLength();
      if (msgLen < CommsConstants.MINIMUM_MESSAGE_SIZE_FOR_CHUNKING)
      {
         // The message is a tiddler, send it in one
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Message is smaller than " + 
                                                        CommsConstants.MINIMUM_MESSAGE_SIZE_FOR_CHUNKING);
         sendEntireMessage(tran, msg, messageSlices, requireReply, jfapPriority);
      }
      else
      {
         // Now we have the data slices, we can start sending the slices in their own message.
         // The JFap channel will guarentee to get the data to the other side or throw us an exception
         // (at some point). As such, we send each chunk (as opposed to exchanging it) and the final 
         // chunk will be exchanged if the requireReply flag was set to true. At that point we will
         // catch any exceptions and throw them on.
         for (int x = 0; x < messageSlices.size(); x++)
         {
            DataSlice slice = messageSlices.get(x);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Sending slice:", slice);
            
            boolean first = (x == 0);
            boolean last = (x == (messageSlices.size() - 1));
            byte flags = 0;
            
            // Work out the flags to send
            if (first) flags |= CommsConstants.CHUNKED_MESSAGE_FIRST;
            if (last)  flags |= CommsConstants.CHUNKED_MESSAGE_LAST;
            else if (!first) flags |= CommsConstants.CHUNKED_MESSAGE_MIDDLE;
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Flags: " + flags);
            
            if (!first)
            {
               // This isn't the first slice, grab a fresh buffer
               request = getCommsByteBuffer();
            }
            
            // Now the normal info. Note we send this everytime as the JFap channel needs dispatchable
            // information derived from this information
            request.putShort(getConnectionObjectID());
            request.putShort(getProxyID());
            request.putSITransaction(tran);
            // Flags to indicate first and last slices
            request.put(flags);
            
            // Now we can dump the slice into the message
            request.putDataSlice(slice);
            
            // And send the message
            if (!last)
            {             
              if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Sending first / middle chunk");

              jfapSend(request, 
                        JFapChannelConstants.SEG_SEND_CHUNKED_SESS_MSG_NOREPLY,
                        jfapPriority, 
                        false,
                        ThrottlingPolicy.BLOCK_THREAD);
            }
            else
            {
               sendData(request, 
                        jfapPriority, 
                        requireReply, 
                        tran,
                        JFapChannelConstants.SEG_SEND_CHUNKED_SESS_MSG,
                        JFapChannelConstants.SEG_SEND_CHUNKED_SESS_MSG_NOREPLY,
                        JFapChannelConstants.SEG_SEND_CHUNKED_SESS_MSG_R);
            }
         }
      }
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "sendChunkedMessage");
   }
   
   /**
    * This helper method is used to send the final or only part of a message to our peer. It takes
    * care of whether we should be exchanging the message and deals with the exceptions returned.
    * 
    * @param request The request buffer
    * @param jfapPriority The JFap priority to send the message
    * @param requireReply Whether we require a reply (or fire-and-forget it)
    * @param tran The transaction being used to send the message (may be null)
    * @param outboundSegmentType The segment type to exchange with
    * @param outboundNoReplySegmentType The segment type to fire-and-forget with
    * @param replySegmentType The segment type to expect on replies
    * 
    * @throws SIResourceException
    * @throws SISessionUnavailableException
    * @throws SINotPossibleInCurrentConfigurationException
    * @throws SIIncorrectCallException
    * @throws SIConnectionUnavailableException
    */
   private void sendData(CommsByteBuffer request, short jfapPriority, boolean requireReply, 
                         SITransaction tran, int outboundSegmentType, int outboundNoReplySegmentType,  
                         int replySegmentType)
      throws SIResourceException, SISessionUnavailableException, 
             SINotPossibleInCurrentConfigurationException, SIIncorrectCallException, 
             SIConnectionUnavailableException
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "sendData", 
                                           new Object[]
                                           {
                                              request, 
                                              jfapPriority, 
                                              requireReply, 
                                              tran, 
                                              outboundSegmentType, 
                                              outboundNoReplySegmentType, 
                                              replySegmentType
                                           });
      
      if (requireReply)
      {
         // Pass on call to server
         CommsByteBuffer reply = jfapExchange(request,
                                              outboundSegmentType, 
                                              jfapPriority, 
                                              false);
         
         try 
         {
            short err = reply.getCommandCompletionCode(replySegmentType);
            if (err != CommsConstants.SI_NO_EXCEPTION)
            {
               checkFor_SISessionUnavailableException(reply, err);
               checkFor_SISessionDroppedException(reply, err);
               checkFor_SIConnectionUnavailableException(reply, err);
               checkFor_SIConnectionDroppedException(reply, err);
               checkFor_SIResourceException(reply, err);
               checkFor_SIConnectionLostException(reply, err);
               checkFor_SILimitExceededException(reply, err);
               checkFor_SINotAuthorizedException(reply, err);
               checkFor_SIIncorrectCallException(reply, err);
               checkFor_SINotPossibleInCurrentConfigurationException(reply, err);
               checkFor_SIErrorException(reply, err);
               defaultChecker(reply, err);
            }
         }
         finally
         {
            if (reply != null) reply.release();
         }
      }
      else
      {
         jfapSend(request, 
                  outboundNoReplySegmentType,
                  jfapPriority, 
                  false,
                  ThrottlingPolicy.BLOCK_THREAD);
                        
         // Update the lowest priority
         if (tran != null)
         {
            ((Transaction) tran).updateLowestMessagePriority(jfapPriority);
         }
      }
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "sendData");
   }

   /**
    * Closes the ProducerSession.
    * 
    * @throws com.ibm.websphere.sib.exception.SIResourceException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
    * @throws com.ibm.websphere.sib.exception.SIErrorException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
    */
   public void close()
      throws SIResourceException, SIConnectionLostException,
             SIErrorException, SIConnectionDroppedException 
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "close");
      
      // Have we already been closed?
      if (!isClosed()) 
      {
         try
         {
            closeLock.writeLock().lockInterruptibly();
            try
            {
               CommsByteBuffer request = getCommsByteBuffer();
               
               // Build Message Header
               request.putShort(getConnectionObjectID());
               request.putShort(getProxyID());
               
               // Pass on call to server
               CommsByteBuffer reply = jfapExchange(request,
                                                    JFapChannelConstants.SEG_CLOSE_PRODUCER_SESS, 
                                                    lowestPriority, 
                                                    true);
               
               try
               {
                  short err = reply.getCommandCompletionCode(JFapChannelConstants.SEG_CLOSE_PRODUCER_SESS_R);
                  if (err != CommsConstants.SI_NO_EXCEPTION)
                  {
                     checkFor_SIConnectionDroppedException(reply, err);
                     checkFor_SIResourceException(reply, err);
                     checkFor_SIConnectionLostException(reply, err);
                     checkFor_SIErrorException(reply, err);
                     defaultChecker(reply, err);
                  }
               }
               finally
               {
                  if (reply != null) reply.release();
               }
               
               setClosed();
               
               if (oc != null) oc.decrementUseCount();
            }
            finally
            {
               closeLock.writeLock().unlock();
            }
         }
         catch (InterruptedException e)
         {
            // No FFDC code needed
         }
      } 
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "close");
   }
   
   /**
    * Updates the sessions lowest message priority. This method is used
    * to keep track of the lowest priority message that we have sent on this
    * session. We need this so that when we flow a close() we can
    * ensure that it will reach the server after any messages. 
    * 
    * @param messagePriorty
    */
   private void updateLowestPriority(short messagePriorty)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "updateLowestPriority", ""+messagePriorty);
      
      // Only update the priority if it is lower than what we
      // already have stored
      if (messagePriorty < this.lowestPriority)
      {
         this.lowestPriority = messagePriorty;
      }
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "updateLowestPriority");
   }
}
