package com.ibm.ws.http.channel.test.api.testobjects;

import com.ibm.ws.http.channel.internal.HttpChannelConfig;
import com.ibm.ws.http.channel.internal.HttpObjectFactory;
import com.ibm.ws.http.channel.internal.outbound.HttpOutboundServiceContextImpl;
import com.ibm.ws.http.channel.test.api.TargetAddress;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.http.channel.outbound.HttpAddress;

/**
 * Testable version of the outbound service context without requiring a full
 * socket connection.
 */
public class MockOutboundSC extends HttpOutboundServiceContextImpl {
    private TargetAddress target = null;
    private final HttpObjectFactory factory = new HttpObjectFactory();

    /**
     * Constructor for a test version of the outbound service context.
     * 
     * @param vc
     * @param cfg
     */
    public MockOutboundSC(VirtualConnection vc, HttpChannelConfig cfg) {
        super(null, null, vc, cfg);
        String portStr = System.getProperty("HTTP_default", "8000");
        int port;
        port = Integer.parseInt(portStr);
        target = new TargetAddress("localhost", port);
    }

    @Override
    public HttpAddress getTargetAddress() {
        return target;
    }

    @Override
    public HttpObjectFactory getObjectFactory() {
        return factory;
    }
}
