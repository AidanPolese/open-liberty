package com.ibm.ws.channelfw.testsuite.chaincoherency;

/**
 * Test factory for int-in int-out.
 */
public class IntDiscInIntDiscOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws Exception
     */
    public IntDiscInIntDiscOutFactory() throws Exception {
        super();
        discType = Integer.class;
        discDataType = Integer.class;
    }
}
