/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer31.async;

import java.io.IOException;

import javax.servlet.ReadListener;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.webcontainer.srt.SRTServletRequestThreadData;
import com.ibm.ws.webcontainer31.osgi.osgi.WebContainerConstants;
import com.ibm.ws.webcontainer31.srt.SRTInputStream31;

import com.ibm.wsspi.channelfw.InterChannelCallback;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;



/**
 *T his class is required when application has set ReadListener on an input stream but the post data has already
 * been fully read as a result of the login procedure.
 * 
 * As all data is read just loop round calling onDataAvailable() until all data is read and then call onAllDataRead.
 * 
 * Added since Servlet 3.1
 * 
 */
public class AsyncAlreadyReadCallback implements InterChannelCallback {

    private final static TraceComponent tc = Tr.register(AsyncAlreadyReadCallback.class, WebContainerConstants.TR_GROUP, LoggerFactory.MESSAGES);

    //The users ReadListener so we can callback to them
    private ReadListener rl;
    //Reference to the SRTInputStream31 that created this particular callback
    private SRTInputStream31 in;
    //ThreadContextManager to push and pop the thread's context data
    private ThreadContextManager threadContextManager;
    private SRTServletRequestThreadData _requestDataAsyncReadCallbackThread;

    public AsyncAlreadyReadCallback(ReadListener rl, SRTInputStream31 in, ThreadContextManager tcm){
        this.rl = rl;
        this.in = in;
        this.threadContextManager = tcm;
        _requestDataAsyncReadCallbackThread = SRTServletRequestThreadData.getInstance();
    }

    /* (non-Javadoc)
     * @see com.ibm.wsspi.channelfw.InterChannelCallback#complete(com.ibm.wsspi.channelfw.VirtualConnection)
     */
    @Override
    @FFDCIgnore(IOException.class)
    public void complete(VirtualConnection vc) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "complete",  vc);
        }
        synchronized( this.in.getCompleteLockObj()){
            
            SRTServletRequestThreadData.getInstance().init(_requestDataAsyncReadCallbackThread);
            
            //Push the original thread's context onto the current thread, also save off the current thread's context
            this.threadContextManager.pushContextData();

            //Call into the user's ReadListener to indicate there is data available
            try{
                if (!in.isFinished()) {
                    rl.onDataAvailable();
                }   
            } catch (Throwable onDataAvailableException){
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Exception occurred during ReadListener.onDataAvailable : " + onDataAvailableException + ", " + rl);
                }
                this.threadContextManager.popContextData();
                error(vc, onDataAvailableException);
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                    Tr.exit(tc, "complete");
                }
                return;
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                 Tr.debug(tc, "Message is fully read, calling ReadListener onAllDataRead : " + rl);
            }
            
            // if all the data has been read we are done.
            if (in.isFinished()) {
                try{
                    rl.onAllDataRead();
                } catch (Throwable onAllDataReadException){
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                       Tr.debug(tc, "Exception occurred during ReadListener.onAllDataRead : " + onAllDataReadException + ", " + rl);
                    }
                    this.threadContextManager.popContextData();
                    error(vc, onAllDataReadException);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                            Tr.exit(tc, "complete");
                    }
                   return;
                }
            } else if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "onDataAavailabe returned without reading all data. Read Listener will no be called again : "  + rl);
             }   
            
            //Revert back to the thread's current context
            this.threadContextManager.popContextData();

        }
        
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "complete");
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.wsspi.channelfw.InterChannelCallback#error(com.ibm.wsspi.channelfw.VirtualConnection, java.lang.Throwable)
     */
    @Override
    public void error(VirtualConnection vc, Throwable t) { 
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Calling user's ReadListener onError : " + rl);
        }
        Exception e = null;
        
        SRTServletRequestThreadData.getInstance().init(_requestDataAsyncReadCallbackThread);
        
        //Push the original thread's context onto the current thread, also save off the current thread's context
        this.threadContextManager.pushContextData();
        
        synchronized( this.in.getCompleteLockObj()){
            try {
                //An error occurred. Issue the onError call on the user's ReadListener
                rl.onError(t);
            } catch (Exception onErrorException) {
                e = onErrorException;
            }
        }
        
        if (e != null && TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Exception occurred during ReadListener.onError : " + e + ", " + rl);
        }
        
        //Revert back to the thread's current context
        this.threadContextManager.popContextData();

    }

}
