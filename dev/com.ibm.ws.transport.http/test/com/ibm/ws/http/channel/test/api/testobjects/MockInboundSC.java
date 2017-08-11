package com.ibm.ws.http.channel.test.api.testobjects;

import com.ibm.ws.http.channel.internal.HttpChannelConfig;
import com.ibm.ws.http.channel.internal.HttpObjectFactory;
import com.ibm.ws.http.channel.internal.HttpRequestMessageImpl;
import com.ibm.ws.http.channel.internal.inbound.HttpInboundServiceContextImpl;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * Testable version of the inbound service context that does not required an
 * underlying socket connection.
 */
public class MockInboundSC extends HttpInboundServiceContextImpl {
    private HttpObjectFactory factory = new HttpObjectFactory();
    private HttpRequestMessageImpl req;

    /**
     * Constructor.
     * 
     * @param vc
     * @param cfg
     */
    public MockInboundSC(VirtualConnection vc, HttpChannelConfig cfg) {
        super(null, null, vc, cfg);
    }

    protected HttpRequestMessageImpl getRequestImpl() {
        if (null == req) {
            req = new MockRequestMessage(this);
        }
        return req;
    }

    public HttpObjectFactory getObjectFactory() {
        return this.factory;
    }

}
