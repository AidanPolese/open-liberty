package com.ibm.ws.http.channel.test.server;

import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.base.InboundApplicationLink;

/**
 * A simple connection link implementation for the HTTP server channel.
 */
@SuppressWarnings("unused")
public class HTTPServerConnLink extends InboundApplicationLink {

    /**
     * Constructor
     * 
     * @param inVC
     */
    public HTTPServerConnLink(VirtualConnection inVC) {
        init(inVC);
        super.vc.getStateMap().put("TestServerConnLink", this);
    }

    public void ready(VirtualConnection inVC) {
        // Queue work into our own worker thread implementation. This is
        // required because the ready method is invoked on the TCPChannel's
        // thread and doing the work required to service a HTTP request may
        // be non-trivial.
        HTTPServerWorkQueue.getRef().queueWork(this);
    }

    public void destroy(Exception e) {
        // we have a connlink that does everything so ignore destroys
    }
}
