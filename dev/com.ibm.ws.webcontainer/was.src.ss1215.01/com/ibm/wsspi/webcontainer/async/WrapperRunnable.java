// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.async;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.webcontainer.async.AsyncListenerEnum;
import com.ibm.ws.webcontainer.async.ListenerHelper;
import com.ibm.ws.webcontainer.async.ListenerHelper.CheckDispatching;
import com.ibm.ws.webcontainer.async.ListenerHelper.ExecuteNextRunnable;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.AsyncContext;

/**
 * 
 * Class that wraps the Runnable an application passes to start(Runnable) so we
 * can add some things for canceling runnables and initializing context.
 * @ibm-private-in-use
 */
public class WrapperRunnable implements Runnable {
	protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.wsspi.webcontainer.async");
	private static final String CLASS_NAME="com.ibm.wsspi.webcontainer.async.WrapperRunnable";
	
	private Runnable runnable;
	private AsyncContext asyncContext;

	public WrapperRunnable(Runnable run, AsyncContext asyncContext) {
		this.runnable = run;
		this.asyncContext = asyncContext;
	}

	@Override
	public void run() {
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST)) { 
			logger.entering(CLASS_NAME,"run",this);
		}
		synchronized(asyncContext){
			asyncContext.removeStartRunnable(this);
		}
		
		//we could try to run this runnable even though it will be removed from the list
		//if the expiration timer executes. Therefore add AtomicBoolean to see if we've already run
		//it or cancelled it.
		if (!getAndSetRunning(true)){
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST)) { 
				logger.logp(Level.FINEST, CLASS_NAME, "run", "running");
			}
			WebContainerRequestState reqState = WebContainerRequestState.getInstance(false);
			if (reqState!=null)
			{
				reqState.init();
			}
			//The spec says "The container MAY take care of the errors from the thread issued via AsyncContext.start."
			//We will catch an error and invoke async error handling, but allow an already dispatched thread to continue processing
			//We do not need to complete the async context as this will be done either by the error handling on the thread which created this (?)
			try {
				runnable.run();
			} catch(Throwable th) {
				logger.logp(Level.WARNING, CLASS_NAME, "run", "error.occurred.during.async.servlet.handling", th);
				ListenerHelper.invokeAsyncErrorHandling(asyncContext, reqState, th, AsyncListenerEnum.ERROR,ExecuteNextRunnable.FALSE,CheckDispatching.TRUE);
			}
		}
		else {
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST)) { 
				logger.logp(Level.FINEST, CLASS_NAME, "run", "not running because it has already ran or been cancelled");
			}
		}
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST)) { 
			logger.exiting(CLASS_NAME,"run",this);
		}
	}
	
	public String toString(){
		return "WrapperRunnable hashCode->" + this.hashCode() + ", start(runnable)->" + runnable;
	}
	
	private AtomicBoolean running = new AtomicBoolean(false);

	public boolean getAndSetRunning(boolean b) {
		return running.getAndSet(b);
	}

}
