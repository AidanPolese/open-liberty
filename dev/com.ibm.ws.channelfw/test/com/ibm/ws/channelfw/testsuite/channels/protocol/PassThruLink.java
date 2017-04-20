package com.ibm.ws.channelfw.testsuite.channels.protocol;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.InboundProtocolLink;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * Test channel that sits above TCP and looks like TCP to those above it.
 */
@SuppressWarnings("unused")
public class PassThruLink extends InboundProtocolLink {
    /**
     * Constructor.
     */
    public PassThruLink() {
        // nothing
    }

    public Object getChannelAccessor() {
        return TCPConnectionContext.class;
    }

    public void ready(VirtualConnection inVC) {
        // nothing
    }

}
