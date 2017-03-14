// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal.inbound;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.http.channel.internal.CallbackIDs;
import com.ibm.ws.http.channel.internal.HttpMessages;
import com.ibm.wsspi.channelfw.InterChannelCallback;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.http.logging.DebugLog;

/**
 * Callback used when the channel is instructed to send a standard
 * HTTP error page back through the sendError() API.
 * 
 */
public class HttpISCWriteErrorCallback implements InterChannelCallback {
    /** RAS tracing variable */
    private static final TraceComponent tc = Tr.register(HttpISCWriteErrorCallback.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /** Singleton instance of the callback */
    private static final HttpISCWriteErrorCallback myInstance = new HttpISCWriteErrorCallback();

    /**
     * Private constructor, use the getRef() API for access.
     */
    private HttpISCWriteErrorCallback() {
        // nothing to do
    }

    /**
     * Get access to the singleton instance.
     * 
     * @return HttpISCWriteErrorCallback
     */
    public static final HttpISCWriteErrorCallback getRef() {
        return myInstance;
    }

    /**
     * Called by the device side channel when the write has finished.
     * 
     * @param vc
     */
    @Override
    public void complete(VirtualConnection vc) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "complete() called: vc=" + vc);
        }
        // The VC might be null if the channel was destroyed before the callback completes
        if (vc != null) {
            HttpInboundServiceContextImpl mySC = (HttpInboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPISC);
            mySC.finishSendError();
        }
    }

    /**
     * Called by the devide side channel when the write had an error.
     * 
     * @param vc
     * @param t
     */
    @Override
    public void error(VirtualConnection vc, Throwable t) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "error() called: vc=" + vc + " t=" + t);
        }

        // The VC might be null if the channel was destroyed before the callback completes
        if (vc != null) {
            HttpInboundServiceContextImpl mySC = (HttpInboundServiceContextImpl) vc.getStateMap().get(CallbackIDs.CALLBACK_HTTPISC);

            if (mySC.getHttpConfig().getDebugLog().isEnabled(DebugLog.Level.WARN)) {
                mySC.getHttpConfig().getDebugLog().log(DebugLog.Level.WARN, HttpMessages.MSG_WRITE_FAIL, mySC);
            }
            mySC.logLegacyMessage();
            // sendError() API already set persistent to false
            mySC.finishSendError();
        }
    }
}
