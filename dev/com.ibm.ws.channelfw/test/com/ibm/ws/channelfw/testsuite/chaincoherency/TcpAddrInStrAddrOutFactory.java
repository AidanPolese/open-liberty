package com.ibm.ws.channelfw.testsuite.chaincoherency;

import com.ibm.wsspi.channelfw.exception.InvalidChannelFactoryException;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;

/**
 * Test factory for tcp-in string-out.
 */
public class TcpAddrInStrAddrOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws InvalidChannelFactoryException
     */
    public TcpAddrInStrAddrOutFactory() throws InvalidChannelFactoryException {
        super();
        devAddr = TCPConnectRequestContext.class;
        appAddrs = new Class[] { String.class };
    }

}
