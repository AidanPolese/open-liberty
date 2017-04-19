package com.ibm.ws.channelfw.testsuite.chaincoherency;

/**
 * Test factory for string-in int-out.
 */
public class IntDiscInStrDiscOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws Exception
     */
    public IntDiscInStrDiscOutFactory() throws Exception {
        super();
        discType = String.class;
        discDataType = Integer.class;
    }
}
