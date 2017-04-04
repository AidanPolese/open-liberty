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
 *This class is required when application has set ReadListener on an input stream.
 * 
 * When the async read requested is completed or get an error at OS the callback is generated from the TCP. 
 * This class will take the appropriate action i.e. call the application API's based on the callback.
 * 
 * Added since Servlet 3.1
 * 
 */
public class AsyncReadCallback implements InterChannelCallback {

    private final static TraceComponent tc = Tr.register(AsyncReadCallback.class, WebContainerConstants.TR_GROUP, LoggerFactory.MESSAGES);

    //The users ReadListener so we can callback to them
    private ReadListener rl;
    //Reference to the SRTInputStream31 that created this particular callback
    private SRTInputStream31 in;
    //ThreadContextManager to push and pop the thread's context data
    private ThreadContextManager threadContextManager;
    private SRTServletRequestThreadData _requestDataAsyncReadCallbackThread;

    public AsyncReadCallback(ReadListener rl, SRTInputStream31 in, ThreadContextManager tcm){
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
        if (null == vc) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                Tr.exit(tc, "complete");
            }
            return;
        }
        //We don't need to do an initial seeding of the buffer here as HTTP Channel has already done that in their
        //complete callback from the TCP Channel. When we call an async read on them they will return indicating
        //there is data to read
        synchronized( this.in.getCompleteLockObj()){

            //This variable was introduced to prevent us from calling into Channel again when there is an outstanding ready
            //Once isReady returns false once, we don't want to change it back until the next call into onDataAvailable
            //This variable prevents isReady from returning true if there is an outstanding read           
            this.in.setAsyncReadOutstanding(false);           
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Calling user's ReadListener onDataAvailable : " + rl);
            }       
            
            SRTServletRequestThreadData.getInstance().init(_requestDataAsyncReadCallbackThread);
            
            //Push the original thread's context onto the current thread, also save off the current thread's context
            this.threadContextManager.pushContextData();

            //Call into the user's ReadListener to indicate there is data available
            try{
                rl.onDataAvailable();
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

            //Determine if the message has been fully read. If so call the user's ReadListener to indicate all data has been read
            //If the message isn't fully read then issue a forced async read to the channel
            if(in.isFinished()){
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Message is fully read, calling ReadListener onAllDataRead : " + rl);
                }
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
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Data hasn't been fully read yet. There should be an outstanding read at this point : " + rl);
                }
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
            this.in.setAsyncReadOutstanding(false);
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
