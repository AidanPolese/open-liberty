package com.ibm.ws.channelfw.testsuite.chaincoherency;

import com.ibm.wsspi.channelfw.exception.InvalidChannelFactoryException;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;

/**
 * Test factory for tcp-in bool/int-out.
 */
public class TcpAddrInBoolIntAddrOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws InvalidChannelFactoryException
     */
    public TcpAddrInBoolIntAddrOutFactory() throws InvalidChannelFactoryException {
        super();
        devAddr = TCPConnectRequestContext.class;
        appAddrs = new Class[] { Boolean.class, Integer.class };
    }
}