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
public class RequestMetadataTest {

    @Test
    public void testNoArgConstructor() {
        RequestMetadata metadata = new RequestMetadata();
        long id = Long.valueOf(metadata.getRequestId());
        metadata = new RequestMetadata();
        assertEquals(Long.valueOf(id + 1), Long.valueOf(metadata.getRequestId()));
    }

    @Test
    public void testMapConstructor() {
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("clientInfo", "someInfo");
        RequestMetadata requestMetadata = new RequestMetadata(metadata);
        assertEquals("someInfo", requestMetadata.getRequestMetadata().get("clientInfo"));
    }

}
