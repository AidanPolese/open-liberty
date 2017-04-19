package com.ibm.ws.channelfw.testsuite.channels.server;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.InboundApplicationLink;

/**
 * Inbound application test link object.
 */
@SuppressWarnings("unused")
public class AppDummyLink extends InboundApplicationLink {

    /**
     * Constructor.
     */
    public AppDummyLink() {
        super();
    }

    public void destroy(Exception e) {
        // nothing
    }

    public void ready(VirtualConnection inVC) {
        init(inVC);
    }

}
