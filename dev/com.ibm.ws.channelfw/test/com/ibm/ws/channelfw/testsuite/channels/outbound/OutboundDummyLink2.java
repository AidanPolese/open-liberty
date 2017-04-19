package com.ibm.ws.channelfw.testsuite.channels.outbound;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.OutboundProtocolLink;

/**
 * Simple outbound link class for test usage.
 */
@SuppressWarnings("unused")
public class OutboundDummyLink2 extends OutboundProtocolLink {
    private OutboundDummyContext context;

    /**
     * Constructor.
     */
    public OutboundDummyLink2() {
        super();
        context = new OutboundDummyContext();
    }

    protected void postConnectProcessing(VirtualConnection conn) {
        // nothing
    }

    public Object getChannelAccessor() {
        return context;
    }

}
