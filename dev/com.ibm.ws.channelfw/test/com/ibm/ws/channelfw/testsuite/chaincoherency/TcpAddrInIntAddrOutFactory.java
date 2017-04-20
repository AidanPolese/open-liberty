package com.ibm.ws.channelfw.testsuite.chaincoherency;

import com.ibm.wsspi.channelfw.exception.InvalidChannelFactoryException;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;

/**
 * Test factory for tcp-in int-out.
 */
public class TcpAddrInIntAddrOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws InvalidChannelFactoryException
     */
    public TcpAddrInIntAddrOutFactory() throws InvalidChannelFactoryException {
        super();
        devAddr = TCPConnectRequestContext.class;
        appAddrs = new Class[] { Integer.class };
    }

}