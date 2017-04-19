package com.ibm.ws.channelfw.testsuite.chaincoherency;

/**
 * Test factory for int-in string-out.
 */
public class StrDiscInIntDiscOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws Exception
     */
    public StrDiscInIntDiscOutFactory() throws Exception {
        super();
        discType = Integer.class;
        discDataType = String.class;
    }
}
