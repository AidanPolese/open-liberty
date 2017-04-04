/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 166828           060603 tevans   Core MP rewrite
 * 169891           180603 gatfora  Addition of trace
 * 170424           240603 tevans   Additional comments
 * 169897.1         300603 tevans   Updates for Milestone 3 Core API
 * 174624.2         260803 gatfora  Refactoring public/private/protected methods 
 * 175097           280803 dware    Know when a callback is running
 * 175640           010902 gatfora  FFDC handling.
 * 180135           201003 gatfora  PROBE ID Refactoring.
 * 181796.1         051103 gatfora  New MS5 Core API
 * 187361.3         050204 tpm      Connection Listeners
 * 196826           080404 gatfora  Connection listener exception should have linked exception
 * 191118           080404 dware    Add ordering context locking
 * 195445.13.5      250504 gatfora  Message content updates
 * 193906.4         260504 prmf     Receive Allowed Async Consumers
 * 217733           220704 gatfora  Improve trace logging.
 * 350261           270306 tpm      Remove deadlock
 * SIB0115.mp.1     260407 ajw      Support pausing/resuming of a messaging endpoint
 * SIB0115.mp.2     060607 ajw      Code review updates for SIB0115
 * 481351           211107 dware    Improve trace around callback calls
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.wsspi.sib.core.AsynchConsumerCallback;
import com.ibm.wsspi.sib.core.LockedMessageEnumeration;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.StoppableAsynchConsumerCallback;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SICoreConnectionListener;
import com.ibm.wsspi.sib.core.ConsumerSession;

/**
 * An AsynchConsumer is a simple wrapper for a registered
 * AsynchConsumerCallback.
 * 
 * @see com.ibm.ws.sib.processor.AsynchConsumerCallback
 * @author rjnorris
 */
public final class AsynchConsumer
{
  //The registered AsynchConsumerCallback
  private AsynchConsumerCallback asynchConsumerCallback = null;
  private boolean asynchConsumerRunning = false;
  
  private static final TraceComponent tc =  
    SibTr.register(
      AsynchConsumer.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
  
  /**
   * Register the AsynchConsumerCallback. If callback is null then
   * this is the equivalent of deregister, i.e. callbackRegistered
   * is set to false.
   * 
   * @param callback
   */
  void registerCallback(AsynchConsumerCallback callback)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "registerCallback", callback);

    asynchConsumerCallback = callback;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "registerCallback");
  }

  /**
   * Calls the registered AsynchConsumerCallback with the given
   * message enumeration.
   * 
   * @param msgEnumeration An enumeration of the locked messages for
   * this AsynchConsumerCallback.
   */
  void processMsgs(LockedMessageEnumeration msgEnumeration, ConsumerSession consumerSession)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "processMsgs", new Object[] { msgEnumeration, consumerSession });      

    // Remember that a callback is running
    asynchConsumerRunning = true;

    try
    {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        SibTr.debug(tc, "Entering asynchConsumerCallback.consumeMessages",
                     new Object[] {asynchConsumerCallback, msgEnumeration});      

      //Call the consumeMessages method on the registered
      //AsynchConsumerCallback object
      asynchConsumerCallback.consumeMessages(msgEnumeration);

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        SibTr.debug(tc, "Exiting asynchConsumerCallback.consumeMessages");      
    }
    catch (Throwable e)
    {
      //Catch any exceptions thrown by consumeMessages.
      FFDCFilter.processException(
       e,
       "com.ibm.ws.sib.processor.impl.AsynchConsumer.processMsgs",
       "1:124:1.37",
       this);
       
      //Notify the consumer that this exception occurred.
      if(consumerSession!=null)
      {
        try
        {
          //Notify the consumer that this exception occurred.
          notifyExceptionListeners(e, consumerSession);
        }
        catch(Exception connectionClosed)
        {
          //No FFDC code needed
        }
            
      }
       
     //Trace the exception but otherwise swallow it since it was not
      //a failure in MP code or code upon which the MP is critically
      //dependent.
      if (e instanceof Exception)
        SibTr.exception(tc, (Exception)e);
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        SibTr.debug(tc, "Exception occurred in consumeMessages " + e);
         
      // We're not allowed to swallow this exception - let it work its way back to
      // the threadpool
      if(e instanceof ThreadDeath)
      {     
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
          SibTr.exit(tc, "processMsgs", e); 
        throw (ThreadDeath)e;
      }
        
    }
    finally
    {
      asynchConsumerRunning = false;
    }
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "processMsgs");
  }
  
  /**
   * Is the callback currently running?
   * @return asynchConsumerRunning
   */
  boolean isAsynchConsumerRunning()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
    {
      SibTr.entry(tc, "isAsynchConsumerRunning");
      SibTr.exit(tc, "isAsynchConsumerRunning",
                 new Boolean(asynchConsumerRunning));
    }
      
    return asynchConsumerRunning;
    
  }

  /**
   * Calls the registered asynchronous exception listeners with the given core
   * exception, or (if not a core exception) wraps exception in a core exception.
   * 
   * @param exception A Throwable exception to give to listeners
   * @param consumerSession Session used to derive listeners
   */
  void notifyExceptionListeners(Throwable coreException, ConsumerSession consumerSession)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "notifyExceptionListeners", new Object[] { coreException, consumerSession });      

    //Notify the consumer that this exception occurred.
    if(consumerSession!=null)
    {
      try
      {

        ConsumerSessionImpl consumerImpl = (ConsumerSessionImpl)consumerSession;
        SICoreConnection connection = consumerImpl.getConnectionInternal();
        SICoreConnectionListener listeners[] = connection.getConnectionListeners();
                  
        for(int listenerIndex=0; listenerIndex<listeners.length; listenerIndex++)
        {
          if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "Entering SICoreConnectionListener.asynchronousException", 
                         new Object[] {listeners[listenerIndex], consumerSession, coreException});      
          
          listeners[listenerIndex].asynchronousException( consumerSession,
                                                          coreException);     

          if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "Exiting SICoreConnectionListener.asynchronousException");       
        }
      }
      catch(Exception connectionClosed)
      {
        //No FFDC code needed

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
          SibTr.debug(tc, "Exception occurred in asynchronousException " + connectionClosed);
      }
    }
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "notifyExceptionListeners");
  }
  
  /*
   * The consumer session has been stopped by the messaging system due to the
   * sequential message failure threshold been reached/exceeded
   */
  void consumerSessionStopped()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "consumerSessionStopped");      

    try
    {
      if (asynchConsumerCallback instanceof StoppableAsynchConsumerCallback)
      {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
          SibTr.debug(tc, "Entering asynchConsumerCallback.consumerSessionStopped",
                       asynchConsumerCallback);      

        ((StoppableAsynchConsumerCallback)asynchConsumerCallback).consumerSessionStopped();

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
          SibTr.debug(tc, "Exiting asynchConsumerCallback.consumerSessionStopped");      
      }
    }
    catch (Throwable e)
    {
      //Catch any exceptions thrown by consumeMessages.
      FFDCFilter.processException(
       e,
       "com.ibm.ws.sib.processor.impl.AsynchConsumer.consumerSessionStopped",
       "1:264:1.37",
       this);

      //Trace the exception but otherwise swallow it since it was not
      //a failure in MP code or code upon which the MP is critically
      //dependent.
      if (e instanceof Exception)
        SibTr.exception(tc, (Exception)e);
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        SibTr.debug(tc, "Exception occurred in consumerSessionStopped " + e);
         
      // We're not allowed to swallow this exception - let it work its way back to
      // the threadpool
      if(e instanceof ThreadDeath)
      {     
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) 
          SibTr.exit(tc, "consumerSessionStopped", e); 
        throw (ThreadDeath)e;
      }
    }
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "consumerSessionStopped");
  }
}
