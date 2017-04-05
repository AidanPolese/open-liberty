// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.async;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.webcontainer.srt.SRTServletRequestThreadData;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;

public class CompleteRunnable implements Runnable {
    
    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.async");
    private static final String CLASS_NAME="com.ibm.ws.webcontainer.async.CompleteRunnable";
    
    private IExtendedRequest iExtendedRequest;
    private AsyncContextImpl asyncContextImpl;
    private SRTServletRequestThreadData requestDataOnCompleteRequestThread;;

    
    public CompleteRunnable(IExtendedRequest extendedRequest,AsyncContextImpl asyncContextImpl) {
        this.iExtendedRequest = extendedRequest;
        this.asyncContextImpl = asyncContextImpl;
        requestDataOnCompleteRequestThread = new SRTServletRequestThreadData();
        requestDataOnCompleteRequestThread.init(SRTServletRequestThreadData.getInstance());
    }

    @Override
    public void run() {
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {            
            logger.entering(CLASS_NAME, "run",this);
        }
    	WebContainerRequestState reqState = WebContainerRequestState.getInstance(false);
    	 
    	 //initialize the request state in case we've already access one previously on the same thread
    	if (reqState!=null)
    		reqState.init(); 
    	
        // Add the request data from the thread on which complete was called to the request data for
        // the thread of the complete runnable.
        SRTServletRequestThreadData.getInstance().init(requestDataOnCompleteRequestThread);
   	
    	try{
    	    invokeOnComplete();
    	}
    	catch(Exception e){
    	    logger.logp(Level.FINE, CLASS_NAME, "run", "There was an exception during onComplete: "+e.getMessage());
    	}
    	finally{
            iExtendedRequest.closeResponseOutput();
            
            asyncContextImpl.setComplete(true);
            
            if (!asyncContextImpl.transferContext()) {
                asyncContextImpl.notifyITransferContextCompleteState();
            }

            asyncContextImpl.invalidate();
            
            //Allows quiesce to finish
            com.ibm.wsspi.webcontainer.WebContainer.getWebContainer().decrementNumRequests();
    
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {            
                logger.exiting(CLASS_NAME, "run",this);
            }
        }
    }
    
    protected void invokeOnComplete() {
    	long endTime = System.currentTimeMillis();
    	long elapsedTime = endTime - asyncContextImpl.getStartTime();
        List<AsyncListenerEntry> list = this.asyncContextImpl.getAsyncListenerEntryList();
        if (list != null) {
            // If there are other listeners: 
            // Give weld or other registered listeners the chance to add a listener to the end of the list.
            if (asyncContextImpl.registerPostEventAsyncListeners()) {
                // then refresh the list to allow for the added ones.
                list = asyncContextImpl.getAsyncListenerEntryList();
            }    
            for (AsyncListenerEntry entry : list) {
                entry.invokeOnComplete(elapsedTime);
            }
        }
    }
}
