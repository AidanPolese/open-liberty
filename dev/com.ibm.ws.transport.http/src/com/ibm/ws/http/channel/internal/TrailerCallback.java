// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.http.channel.internal.inbound.HttpInboundServiceContextImpl;
import com.ibm.ws.http.channel.internal.outbound.HttpOutboundServiceContextImpl;
import com.ibm.wsspi.channelfw.OutboundVirtualConnection;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPReadCompletedCallback;
import com.ibm.wsspi.tcpchannel.TCPReadRequestContext;

/**
 * Trailer header parsing will use this callback when the reads are performed
 * asynchronously. This callback will pick up with the parsing logic once a
 * read has completed.
 */
public class TrailerCallback implements TCPReadCompletedCallback {

    /** RAS tracing variable */
    private static final TraceComponent tc = Tr.register(TrailerCallback.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /** Singleton instance of the class */
    private static final TrailerCallback myInstance = new TrailerCallback();

    /**
     * Private constructor, use the getRef() API for access.
     */
    private TrailerCallback() {
        // nothing to do
    }

    /**
     * Get access to the singleton instance of the class.
     * 
     * @return TrailerCallback
     */
    public static final TrailerCallback getRef() {
        return myInstance;
    }

    /**
     * Called by the device side channel when the read has completed.
     * 
     * @param vc
     * @param rsc
     */
    public void complete(VirtualConnection vc, TCPReadRequestContext rsc) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "complete() called: vc=" + vc);
        }
        // all we have to do is get the service context and continue the read
        // process

        if (vc instanceof OutboundVirtualConnection) {
            HttpOutboundServiceContextImpl osc = (HttpOutboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPOSC);
            osc.continueRead();
        } else {
            HttpInboundServiceContextImpl isc = (HttpInboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPISC);
            isc.continueRead();
        }
    }

    /**
     * Called by the device side channel when an error occurred.
     * 
     * @param vc
     * @param rsc
     * @param ioe
     */
    public void error(VirtualConnection vc, TCPReadRequestContext rsc, IOException ioe) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "error() called: vc=" + vc + " ioe=" + ioe);
        }
        // get access to the service context and then pass along this error to
        // the app channel above

        if (vc instanceof OutboundVirtualConnection) {
            HttpOutboundServiceContextImpl osc = (HttpOutboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPOSC);
            osc.setPersistent(false);
            osc.getAppReadCallback().error(vc, ioe);
        } else {
            HttpInboundServiceContextImpl isc = (HttpInboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPISC);
            isc.setPersistent(false);
            isc.getAppReadCallback().error(vc, ioe);
        }
    }
}