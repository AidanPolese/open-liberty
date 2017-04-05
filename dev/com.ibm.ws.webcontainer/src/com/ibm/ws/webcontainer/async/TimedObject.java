// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// CHANGE HISTORY
// Defect       Date        Modified By         Description
//--------------------------------------------------------------------------------------
// 464556    2007/09/05     mmolden             70FVT : setExpirationTimeoutOverride not working.                                                                            
//

package com.ibm.ws.webcontainer.async;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.webcontainer.async.ListenerHelper.CheckDispatching;
import com.ibm.ws.webcontainer.async.ListenerHelper.ExecuteNextRunnable;
import com.ibm.wsspi.webcontainer.servlet.AsyncContext;


public class TimedObject {
    private static Logger logger= Logger.getLogger("com.ibm.ws.webcontainer.async");
    private static final String CLASS_NAME="com.ibm.ws.webcontainer.async.TimedObject";
	private TimerTask task;
	private AsyncContext asyncContext;
    boolean invokedErrorHandling = false;
	private AsyncServletReentrantLock asyncServletReentrantLock;
	
	public TimedObject	(AsyncContext asyncContext, List<AsyncListenerEntry> asyncListenerEntryList){
		if (logger.isLoggable(Level.FINEST)) {
            logger.logp(Level.FINEST,CLASS_NAME, "<init>","this->"+this+", asyncContext->"+asyncContext)    ;   
      }
		this.asyncContext = asyncContext;
        this.asyncServletReentrantLock = asyncContext.getErrorHandlingLock();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.wsspi.asynchttp.queue.IAsyncEntry#getTimerTask()
	 */
	public TimerTask getTimerTask()
	{
            if (logger.isLoggable(Level.FINEST)) {
                   logger.entering(CLASS_NAME, "getTimerTask")    ;
                   logger.exiting(CLASS_NAME, "getTimerTask")    ;
            }
            if (task==null)
            	task = new EntryTimer();
		return task;
	}

	public boolean cancelTimer()
	{
            
            
            boolean ret = true;
		    synchronized(asyncContext){ 
		    	if (logger.isLoggable(Level.FINEST)) {
	                   logger.entering(CLASS_NAME, "cancelTimer");   
	                   logger.logp(Level.FINEST,CLASS_NAME, "cancelTimer","this->"+this+", timer task->"+task+", invokedErrorHandling->"+invokedErrorHandling)    ;   
	             }
				if(task != null)
				{
					
					ret = task.cancel();
					task=null;
				} else {
					ret = !invokedErrorHandling; //if the task is already set to null, but we didn't invoke error handling, then there was no timeout
				}
		    }
            
            if (logger.isLoggable(Level.FINEST)) {
                 Object [] obj = {task, Boolean.valueOf(ret)};
                  logger.exiting(CLASS_NAME, "cancelTimer", obj)    ;   
            }

		return ret;
	}
	
	 protected void processExpiredEntry() {
		 if (logger.isLoggable(Level.FINEST)) {
			 	logger.entering(CLASS_NAME, "processExpiredEntry");
			 	logger.logp(Level.FINEST,CLASS_NAME, "processExpiredEntry","this->"+this+", asyncContext->"+asyncContext);   
	      }

		
		 synchronized(asyncContext){
	    	 if (task!=null){
	    		 invokedErrorHandling = true;
	    		 task=null;
	    	 }
		 }
		 
		 if (invokedErrorHandling){
			 //executeNextRunnable is true because this is called outside of webcontainer thread.
			 //check dispatching is true because you could start a dispatch right after the timeout kicked off,
			 //pass the lock originally retrieved for this go round to see if async error handling already was invoked.
			 ListenerHelper.invokeAsyncErrorHandling(asyncContext, null, null, AsyncListenerEnum.TIMEOUT,ExecuteNextRunnable.TRUE,CheckDispatching.TRUE,asyncServletReentrantLock);
		 }

         if (logger.isLoggable(Level.FINEST)) {
			 	logger.exiting(CLASS_NAME, "processExpiredEntry");
	     }
     }
	
	/**
	 * EntryTimer is inner class for managing timer transactions. An inner
	 * class had to be used in order to pool this object.  TimerTask objects
	 * can only be used once and cannot be reset or pooled.
	 * 
	 */
	protected class EntryTimer extends TimerTask
	{
		public void run()
		{
            if (logger.isLoggable(Level.FINEST)) {     
                logger.entering(CLASS_NAME, "run");                         
            }
            
            
            processExpiredEntry();
            
            
            if (logger.isLoggable(Level.FINEST)) {     
                 logger.exiting(CLASS_NAME, "run");                         
             }

		}

       
	}
}
