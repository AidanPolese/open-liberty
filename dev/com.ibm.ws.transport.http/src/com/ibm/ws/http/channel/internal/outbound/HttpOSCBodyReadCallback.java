// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal.outbound;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.http.channel.internal.CallbackIDs;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPReadCompletedCallback;
import com.ibm.wsspi.tcpchannel.TCPReadRequestContext;

/**
 * Callback used while reading the incoming body of the response message.
 */
public class HttpOSCBodyReadCallback implements TCPReadCompletedCallback {
    /** RAS tracing variable */
    private static final TraceComponent tc = Tr.register(HttpOSCBodyReadCallback.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /** Singleton object */
    private static final HttpOSCBodyReadCallback myInstance = new HttpOSCBodyReadCallback();

    /**
     * Private constructor, use the getRef() method.
     */
    private HttpOSCBodyReadCallback() {
        // nothing to do
    }

    /**
     * Get access to the singleton instance of this class.
     * 
     * @return HttpOSCBodyReadCallback
     */
    public static final HttpOSCBodyReadCallback getRef() {
        return myInstance;
    }

    /**
     * Called by the channel below us when a read has finished.
     * 
     * @param vc
     * @param rsc
     */
    public void complete(VirtualConnection vc, TCPReadRequestContext rsc) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "complete() called: vc=" + vc);
        }
        HttpOutboundServiceContextImpl mySC = (HttpOutboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPOSC);
        mySC.continueRead();
    }

    /**
     * Called by the channel below us when an error occurs during a read.
     * 
     * @param vc
     * @param rsc
     * @param ioe
     */
    public void error(VirtualConnection vc, TCPReadRequestContext rsc, IOException ioe) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "error() called: vc=" + vc + " ioe=" + ioe);
        }
        HttpOutboundServiceContextImpl mySC = (HttpOutboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPOSC);

        // Reading a body from an HTTP/1.0 server that closes the connection
        // after completely sending the body will trigger an IOException here.
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Socket closure, signaling close.");
        }
        // only valid if this response is a non-length delimited body
        // otherwise pass the exception up to the application channel

        // PK18799 - check the stored SC values instead of msg as Proxy may
        // change it on the fly
        if (!mySC.isChunkedEncoding() && !mySC.isContentLength()) {
            mySC.prepareClosure();
            mySC.getAppReadCallback().complete(vc);
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IOException during body read");
            }
            mySC.setPersistent(false);
            mySC.getAppReadCallback().error(vc, ioe);
        }
    }
}
