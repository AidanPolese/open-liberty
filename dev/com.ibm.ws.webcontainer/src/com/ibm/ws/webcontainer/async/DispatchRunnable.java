// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// CHANGE HISTORY
//      Defect          Date            Modified By             Description
//--------------------------------------------------------------------------------------
//      PM90834         07/25/13        bowitten                ASYNC SERVLET LOST ORIGINAL IDENTITY AFTER RESUME

package com.ibm.ws.webcontainer.async;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.ws.webcontainer.async.ListenerHelper.CheckDispatching;
import com.ibm.ws.webcontainer.async.ListenerHelper.ExecuteNextRunnable;
import com.ibm.ws.webcontainer.srt.SRTServletRequestThreadData;
import com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;

public class DispatchRunnable extends ServiceWrapper implements Runnable {
    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.async");
    private static final String CLASS_NAME = "com.ibm.ws.webcontainer.async.DispatchRunnable";

    private WebAppRequestDispatcher requestDispatcher;
    private ServletRequest servletRequest;
    private ServletResponse servletResponse;
    private IExtendedRequest extRequest;
    private AsyncContextImpl asyncContextImpl;    
    private SRTServletRequestThreadData requestDataOnDispatchRequestThread;


    public DispatchRunnable(WebAppRequestDispatcher requestDispatcher, AsyncContextImpl asyncContextImpl) {
        super(asyncContextImpl);
        this.requestDispatcher = requestDispatcher;
        this.asyncContextImpl = asyncContextImpl;
        this.servletRequest = asyncContextImpl.getRequest();
        this.servletResponse = asyncContextImpl.getResponse();
        this.extRequest = asyncContextImpl.getIExtendedRequest();
        requestDataOnDispatchRequestThread = new SRTServletRequestThreadData();
        requestDataOnDispatchRequestThread.init(SRTServletRequestThreadData.getInstance());
    }


    public void run() {       

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {
			logger.entering(CLASS_NAME, "run");
		}
    	
        // Start:PM90834
        if (!asyncContextImpl.transferContext()) {
            popContextData();
        } 
        // End:PM90834
        
        WebContainerRequestState reqState = WebContainerRequestState.createInstance();
        try {            
            
            // We know we need the reqState so its better to call
            // createInstance than to worry about calling init() after
            // getInstance(true)
            // which can be wasteful if the instance already existed
            reqState.setCurrentThreadsIExtendedRequest(extRequest);

            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {
                logger.logp(Level.FINEST, CLASS_NAME, "run", "Context Class loader before dispatch: " + Thread.currentThread().getContextClassLoader());
            }
            
            // Add the request data from the thread on which dispatch was called to the request data for
            // the thread of the complete runnable.
            SRTServletRequestThreadData.getInstance().init(requestDataOnDispatchRequestThread);
            
            //PI28910 - the dispatch now takes effect so set asyncStarted to false
            extRequest.setAsyncStarted(false);

            requestDispatcher.dispatch(this.servletRequest, this.servletResponse, DispatcherType.ASYNC);

        } catch (Throwable th) {
        	//This one shouldn't get called as it should get handled in invokeFilters inside of the dispatch
        	logger.logp(Level.WARNING, CLASS_NAME, "run", "error.calling.async.dispatch", th);
        	//Don't call executeNextRunnable because that will be called below
        	
        	//Don't check dispatching because we know its true and want to invoke error handling anyway because we are in control
        	//of when it is invoked.
        	ListenerHelper.invokeAsyncErrorHandling(asyncContextImpl, reqState, th, AsyncListenerEnum.ERROR,ExecuteNextRunnable.FALSE,CheckDispatching.FALSE);
        	
        } finally {
            // Put this stuff in the finally in case the dispatch throws an exception

        	// Start:PM90834
        	if (!asyncContextImpl.transferContext()) {
                    resetContextData();
        	}
        	// End:PM90834

            // call complete if it wasn't already called for the async context that initiated the dispatch
            synchronized(asyncContextImpl){ 
                    //sync on asyncContext so we don't get inconsistent results between if block and calling complete
	            if (!asyncContextImpl.isComplete()&&!reqState.isAsyncMode()&&!asyncContextImpl.isCompletePending()){
	            	//complete won't call executeNextRunnabled because dispatching will return true
	                asyncContextImpl.complete();
	            }
        	}
            
            //Since complete won't call executeNextRunnable, call it here.
            asyncContextImpl.executeNextRunnable();
                
        }
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {
			logger.exiting(CLASS_NAME, "run");
		}
    	
    }


}
