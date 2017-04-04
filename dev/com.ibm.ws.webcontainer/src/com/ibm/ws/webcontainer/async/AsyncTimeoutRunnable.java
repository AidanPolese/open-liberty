package com.ibm.ws.webcontainer.async;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.webcontainer.async.ListenerHelper.CheckDispatching;
import com.ibm.ws.webcontainer.async.ListenerHelper.ExecuteNextRunnable;
import com.ibm.ws.webcontainer.srt.SRTServletRequestThreadData;
import com.ibm.wsspi.webcontainer.servlet.AsyncContext;

public class AsyncTimeoutRunnable implements Runnable {
	private static Logger logger= Logger.getLogger("com.ibm.ws.webcontainer.async");
    private static final String CLASS_NAME="com.ibm.ws.webcontainer.async.AsyncTimeoutRunnable";
	private AsyncContext asyncContext;
	private AsyncServletReentrantLock asyncServletReentrantLock;
        private SRTServletRequestThreadData requestDataOnTimedOutThread;
	
	public AsyncTimeoutRunnable	(AsyncContext asyncContext){
		if (logger.isLoggable(Level.FINEST)) {
                    logger.logp(Level.FINEST,CLASS_NAME, "<init>","this->"+this+", asyncContext->"+asyncContext)    ;   
                }
		this.asyncContext = asyncContext;
                this.asyncServletReentrantLock = asyncContext.getErrorHandlingLock();
                requestDataOnTimedOutThread = new SRTServletRequestThreadData();
                requestDataOnTimedOutThread.init(SRTServletRequestThreadData.getInstance());
	}
		
	@Override
	public void run() {
	        SRTServletRequestThreadData.getInstance().init(requestDataOnTimedOutThread);
		ListenerHelper.invokeAsyncErrorHandling(this.asyncContext, null, null, AsyncListenerEnum.TIMEOUT,ExecuteNextRunnable.TRUE,CheckDispatching.TRUE,this.asyncServletReentrantLock);
	}

}
