/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013,2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.rest.handler.internal.helper;

import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Test;

import com.ibm.ws.rest.handler.helper.ServletRESTResponseImpl;
import com.ibm.wsspi.rest.handler.RESTResponse;

/**
 *
 */
public class RESTResponseImplTest {
    private final Mockery mock = new JUnit4Mockery();
    private final HttpServletResponse httpResponse = mock.mock(HttpServletResponse.class);

    private final RESTResponse restResponse = new ServletRESTResponseImpl(httpResponse);

    @After
    public void tearDown() {
        mock.assertIsSatisfied();
    }

    /**
     * Test method for {@link com.ibm.ws.rest.handler.helper.ServletRESTResponseImpl#getWriter()}.
     */
    @Test
    public void getWriter() throws Exception {
        mock.checking(new Expectations() {
            {
                one(httpResponse).getWriter();
                will(returnValue(null));
            }
        });

        assertNull("FAIL: the mock was supposed to return null, and we should get that back",
                   restResponse.getWriter());
    }

    /**
     * Test method for {@link com.ibm.ws.rest.handler.helper.ServletRESTResponseImpl#setResponseHeader(java.lang.String, java.lang.String)}.
     */
    @Test
    public void setResponseHeader() throws Exception {
        mock.checking(new Expectations() {
            {
                one(httpResponse).setHeader("testHeader", "testValue");
            }
        });

        restResponse.setResponseHeader("testHeader", "testValue");
    }

    /**
     * Test method for {@link com.ibm.ws.rest.handler.helper.ServletRESTResponseImpl#setStatus(int)}.
     */
    @Test
    public void setStatus() throws Exception {
        mock.checking(new Expectations() {
            {
                one(httpResponse).setStatus(1);
            }
        });

        restResponse.setStatus(1);
    }

}
