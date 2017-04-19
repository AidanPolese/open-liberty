package com.ibm.ws.channelfw.testsuite.channels.outbound;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.OutboundProtocolLink;

/**
 * Test outbound application link object.
 */
@SuppressWarnings("unused")
public class GetterLink extends OutboundProtocolLink {

    /**
     * Constructor.
     */
    public GetterLink() {
        super();
    }

    protected void postConnectProcessing(VirtualConnection conn) {
        // nothing
    }

    public Object getChannelAccessor() {
        return null;
    }

}
