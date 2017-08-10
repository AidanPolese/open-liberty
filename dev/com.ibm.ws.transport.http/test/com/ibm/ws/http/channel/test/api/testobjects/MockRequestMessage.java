package com.ibm.ws.http.channel.test.api.testobjects;

import com.ibm.ws.http.channel.internal.HttpRequestMessageImpl;
import com.ibm.ws.http.channel.internal.HttpServiceContextImpl;
import com.ibm.wsspi.http.channel.values.SchemeValues;

/**
 * Testable version of the HTTP request message that does not require an
 * underlying socket connection.
 */
public class MockRequestMessage extends HttpRequestMessageImpl {
    private HttpServiceContextImpl mySC;

    /**
     * Constructor.
     * 
     * @param sc
     */
    public MockRequestMessage(HttpServiceContextImpl sc) {
        super();
        mySC = sc;
        init(sc);
    }

    public void initScheme() {
        setScheme(SchemeValues.HTTP);
    }

    protected byte[] getResource() {
        return getRequestURIAsByteArray();
    }

    public HttpServiceContextImpl getServiceContext() {
        return this.mySC;
    }

}
