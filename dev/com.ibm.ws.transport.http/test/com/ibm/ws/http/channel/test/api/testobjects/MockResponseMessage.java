package com.ibm.ws.http.channel.test.api.testobjects;

import com.ibm.ws.http.channel.internal.HttpResponseMessageImpl;
import com.ibm.ws.http.channel.internal.HttpServiceContextImpl;

/**
 * Testable version of the response message that does not require an
 * underlying socket connection.
 */
public class MockResponseMessage extends HttpResponseMessageImpl {
    /**
     * Constructor.
     * 
     * @param sc
     */
    public MockResponseMessage(HttpServiceContextImpl sc) {
        super();
        init(sc);
    }

}
