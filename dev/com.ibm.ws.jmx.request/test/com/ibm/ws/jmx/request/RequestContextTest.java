/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.request;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 *
 */
public class RequestContextTest {

    private long requests = Long.valueOf(new RequestMetadata().getRequestId());

    @Test
    public void testNoId() {
        requests += 1;
        assertEquals("no ID", RequestContext.getRequestMetadata().getRequestId());

    }

    @Test
    public void testMetadata() {
        try {
            requests += 1;
            Map<String, Object> metadata = new HashMap<String, Object>();
            metadata.put("foo", "bar");
            RequestMetadata requestMetadata = new RequestMetadata(metadata);
            RequestContext.setRequestMetadata(requestMetadata);
            assertEquals(String.valueOf(requests), RequestContext.getRequestMetadata().getRequestId());
            assertEquals("bar", RequestContext.getRequestMetadata().getRequestMetadata().get("foo"));
        } finally {
            RequestContext.removeRequestMetadata();
        }
    }
}
