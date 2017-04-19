package com.ibm.ws.channelfw.testsuite.chaincoherency;

/**
 * Test factory for string-in string-out.
 */
public class StrDiscInStrDiscOutFactory extends ConnectorChannelFactory {
    /**
     * Constructor.
     * 
     * @throws Exception
     */
    public StrDiscInStrDiscOutFactory() throws Exception {
        super();
        discType = String.class;
        discDataType = String.class;
    }
}
