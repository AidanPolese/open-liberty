package com.ibm.ws.channelfw.testsuite.chaincoherency;

import com.ibm.wsspi.channelfw.exception.InvalidChannelFactoryException;

/**
 * Test factory for string-in int-out.
 */
public class StrAddrInIntAddrOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws InvalidChannelFactoryException
     */
    public StrAddrInIntAddrOutFactory() throws InvalidChannelFactoryException {
        super();
        devAddr = String.class;
        appAddrs = new Class[] { Integer.class };
    }
}