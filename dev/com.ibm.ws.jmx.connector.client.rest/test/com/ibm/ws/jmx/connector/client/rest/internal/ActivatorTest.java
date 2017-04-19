/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.jmx.connector.client.rest.internal;

import static com.ibm.ws.jmx.connector.client.rest.internal.Activator.add;
import static com.ibm.ws.jmx.connector.client.rest.internal.Activator.remove;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 */
public class ActivatorTest {

    @Test
    public void testPattern() throws Exception {
        assertEquals("a", remove(add("a")));
        assertEquals("a.com.ibm.ws.jmx.connector.client", remove(add("a.com.ibm.ws.jmx.connector.client")));
        assertEquals("a.com.ibm.ws.jmx.connector.client.b", remove(add("a.com.ibm.ws.jmx.connector.client.b")));
        assertEquals("com.ibm.ws.jmx.connector.client.b", remove(add("com.ibm.ws.jmx.connector.client.b")));
    }

}
