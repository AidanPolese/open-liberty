/*
 * @start_prolog@
 * Version: @(#) 1.25 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/BrowserSessionProxy.java, SIB.comms, WASX.SIB, uu1215.01 11/06/29 06:57:35 [4/12/12 22:14:05]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2004, 2011 
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
 * Creation        030702 prestona Original
 * d169897.2       030707 schmittm Provide remote client implementation of new Core API as defined
 * F171893         030718 prestona Add BrowserSession support on client.
 * d174326         030814 mattheg  Organise imports to fix compile warnings.
 * F174602         030819 prestona Switch to using SICommsException
 * f173765.2       030925 mattheg  Core API M4 update
 * f179339.4       031222 mattheg  Forward and reverse routing support
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * d189716         040218 mattheg  FFDC Instrumentation
 * d187252         040302 mattheg  Ensure session destination information is only returned if it changes
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D276260         050516 mattheg  Add hashcode to trace (not change flagged)
 * D377648         060719 mattheg  Use CommsByteBuffer
 * PM42438         280611 ajw      Use Reentrant write/read lock rather than a non reentrant write/read lock
 * ============================================================================
 */
 
package com.ibm.ws.sib.comms.client;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.client.proxyqueue.BrowserProxyQueue;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.mfp.MessageDecodeFailedException;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.BrowserSession;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;


/**
 * A client implementation of the Core API Browser Session.  This
 * object implements the BrowserSession interface with network
 * aware code.  The implementation "knows" how to provide browser
 * session functionality in a client environment.
 */
public class BrowserSessionProxy extends DestinationSessionProxy implements BrowserSession      // f179339.4
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = BrowserSessionProxy.class.getName();
   
   /** Trace */
   private static final TraceComponent tc = SibTr.register(BrowserSessionProxy.class, 
                                                           CommsConstants.MSG_GROUP, 
                                                           CommsConstants.MSG_BUNDLE);

   // The proxy queue which backs this browser session.   
   private BrowserProxyQueue proxyQueue;

   // Reader/writer lock used to provide mutual exclusion for the close operation.
   // Close itself must obtain this lock as a writer.  Most other methods from
   // the BrowserSession interface need to obtain this as a reader.   
   private ReentrantReadWriteLock closeLock = null;
   
   // Lock used to provide synchronization for session methods.  We cannot
   // synchronize on this as it might cause "unexpected" behaviour for the user.
   private Object lock = new Object();
   
   /**
    * Constructor.
    * 
    * @param con
    * @param cp
    * @param data
    * @param proxy
    * @param destAddr
    */
   protected BrowserSessionProxy(Conversation con, ConnectionProxy cp, CommsByteBuffer data,
                                 BrowserProxyQueue proxy, SIDestinationAddress destAddr)
   {
      super(con, cp);
      
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", 
                                           new Object[]{con, cp, data, proxy, destAddr});
      
      setDestinationAddress(destAddr);
      inflateData(data);
      closeLock = cp.closeLock;
      proxyQueue = proxy;

      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
   }

   /**
    * Browses the next message. Aside from locking concerns, this simply delegates to the proxy
    * queue "next" implementation.
    * 
    * @see BrowserProxyQueue#next()
    * 
    * @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
    * @throws com.ibm.websphere.sib.exception.SIResourceException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
    * @throws com.ibm.websphere.sib.exception.SIErrorException
    * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
    */
   // f169897.2 changed throws   
   public SIBusMessage next() 
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, 
             SIErrorException,
             SINotAuthorizedException
	{
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "next");
      
      checkAlreadyClosed();
      
      SIBusMessage message = null;
      synchronized(lock)
      {
         try
         {
            closeLock.readLock().lockInterruptibly();
            try
            {
               message = proxyQueue.next();
            }
            catch(MessageDecodeFailedException mde)
            {
               FFDCFilter.processException(mde, CLASS_NAME + ".next", 
                                           CommsConstants.BROWSERSESSION_NEXT_01, this);
               SIResourceException coreException = new SIResourceException(mde);
               throw coreException;
            }
            finally
            {
               closeLock.readLock().unlock();
            }
         }
         catch(InterruptedException e)
         {
            // No FFDC code needed
            if (tc.isDebugEnabled()) SibTr.debug(this, tc, "interrupted exception");
         }
      }
      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "next", message);
      return message;
	}

   /**
    * Closes the browser session.  This involves some synchronization, flowing a
    * close request and also marking the appropriate objects as closed.
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
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "close");
      synchronized(lock)
      {
         if (!isClosed())
         {
            try
            {
               closeLock.writeLock().lockInterruptibly();
               try
               {
                  // Close the proxy queue
                  proxyQueue.close();
                
                  // Mark this session as closed.
                  setClosed();  
               }
               finally
               {
                  closeLock.writeLock().unlock();
               }
            }
            catch(InterruptedException e)
            {
               // No FFDC code needed
               if (tc.isDebugEnabled()) SibTr.debug(this, tc, "interrupted exception");
            }
         }
      }      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "close");
	}

   /**
    * Resets the browse cursor.  Here, we simply delegate to the proxy
    * queue which backs the browser session.
    * 
    * @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
    * @throws com.ibm.websphere.sib.exception.SIResourceException
    * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
    * @throws com.ibm.websphere.sib.exception.SIErrorException
    */
	//f169897.2 added
   public void reset()
      throws SISessionUnavailableException, SISessionDroppedException,
             SIConnectionUnavailableException, SIConnectionDroppedException,
             SIResourceException, SIConnectionLostException, 
             SIErrorException
	{
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "reset");
      
      checkAlreadyClosed();
      
      synchronized(lock)
      {
         try
         {
            closeLock.readLock().lockInterruptibly();
            try
            {
               proxyQueue.reset();
            }
            finally
            {
               closeLock.readLock().unlock();
            }

         }
         catch(InterruptedException e)
         {
            // No FFDC code needed
            if (tc.isDebugEnabled()) SibTr.debug(this, tc, "interrupted exception");
         }
      }
      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "reset");
	}
}
