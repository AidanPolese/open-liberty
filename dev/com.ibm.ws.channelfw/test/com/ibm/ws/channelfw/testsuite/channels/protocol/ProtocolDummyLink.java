package com.ibm.ws.channelfw.testsuite.channels.protocol;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.InboundProtocolLink;

/**
 * Dummy protocol-type connection link object.
 */
@SuppressWarnings("unused")
public class ProtocolDummyLink extends InboundProtocolLink {
    private ProtocolDummyContext context = null;

    /**
     * Constructor.
     */
    public ProtocolDummyLink() {
        this.context = new ProtocolDummyContext();
    }

    public Object getChannelAccessor() {
        return this.context;
    }

    public void ready(VirtualConnection inVC) {
        //
    }

}
