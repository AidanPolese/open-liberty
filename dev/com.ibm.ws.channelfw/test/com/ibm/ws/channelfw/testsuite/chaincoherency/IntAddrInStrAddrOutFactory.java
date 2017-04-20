package com.ibm.ws.channelfw.testsuite.chaincoherency;

import com.ibm.wsspi.channelfw.exception.InvalidChannelFactoryException;

/**
 * Test factory for int-in str-out.
 */
public class IntAddrInStrAddrOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor
     * 
     * @throws InvalidChannelFactoryException
     */
    public IntAddrInStrAddrOutFactory() throws InvalidChannelFactoryException {
        super();
        devAddr = Integer.class;
        appAddrs = new Class[] { String.class };
    }
}