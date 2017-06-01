/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.wim.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ibm.wsspi.security.wim.exception.InvalidUniqueNameException;

public class UniqueNameHelperTest {

    @Test
    public void formatUniqueName() throws Exception {
        try {
            UniqueNameHelper.formatUniqueName("invaliduniquename");
            fail("Expected InvalidUniqueNameException.");
        } catch (InvalidUniqueNameException e) {
            /* Passed. */
        }

        /*
         * Test some of the space handling in getValidDN.
         */
        assertEquals("uid=user1,dc=ibm,dc=com", UniqueNameHelper.formatUniqueName("uid=user1,dc=ibm,dc=com"));
        assertEquals("uid=user1 \\ ,dc=ibm,dc=com", UniqueNameHelper.formatUniqueName("uid=user1 \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1    \\ ,dc=ibm,dc=com", UniqueNameHelper.formatUniqueName("uid=user1\\ \\ \\ \\ \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1,dc=ibm,dc=com    \\ ", UniqueNameHelper.formatUniqueName("uid=user1,dc=ibm,dc=com\\ \\ \\ \\ \\ "));
    }

    @Test
    public void getRDNs() throws Exception {
        assertArrayEquals(new String[] {}, UniqueNameHelper.getRDNs(""));
        assertArrayEquals(new String[] { "uid" }, UniqueNameHelper.getRDNs("uid"));
        assertArrayEquals(new String[] { "uid", "email" }, UniqueNameHelper.getRDNs("uid+email"));
        assertArrayEquals(new String[] { "uid", "email", "phone" }, UniqueNameHelper.getRDNs("uid+email+phone"));
    }

    @Test
    public void getValidUniqueName() throws Exception {
        assertNull(UniqueNameHelper.getValidUniqueName("invaliduniquename"));

        /*
         * Test some of the space handling in getValidDN.
         */
        assertEquals("uid=user1,dc=ibm,dc=com", UniqueNameHelper.getValidUniqueName("uid=user1,dc=ibm,dc=com"));
        assertEquals("uid=user1 \\ ,dc=ibm,dc=com", UniqueNameHelper.getValidUniqueName("uid=user1 \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1    \\ ,dc=ibm,dc=com", UniqueNameHelper.getValidUniqueName("uid=user1\\ \\ \\ \\ \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1,dc=ibm,dc=com    \\ ", UniqueNameHelper.getValidUniqueName("uid=user1,dc=ibm,dc=com\\ \\ \\ \\ \\ "));
    }

    @Test
    public void isDN() throws Exception {
        assertNull(UniqueNameHelper.isDN(null));
        assertNull(UniqueNameHelper.isDN("invaliduniquename"));

        /*
         * Test some of the space handling in getValidDN.
         */
        assertEquals("uid=user1,dc=ibm,dc=com", UniqueNameHelper.isDN("uid=user1,dc=ibm,dc=com"));
        assertEquals("uid=user1 \\ ,dc=ibm,dc=com", UniqueNameHelper.isDN("uid=user1 \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1    \\ ,dc=ibm,dc=com", UniqueNameHelper.isDN("uid=user1\\ \\ \\ \\ \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1,dc=ibm,dc=com    \\ ", UniqueNameHelper.isDN("uid=user1,dc=ibm,dc=com\\ \\ \\ \\ \\ "));
    }

    @Test
    public void unescapeSpaces() {
        /*
         * Test some of the space handling in getValidDN.
         */
        assertEquals("uid=user1,dc=ibm,dc=com", UniqueNameHelper.unescapeSpaces("uid=user1,dc=ibm,dc=com"));
        assertEquals("uid=user1 \\ ,dc=ibm,dc=com", UniqueNameHelper.unescapeSpaces("uid=user1 \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1    \\ ,dc=ibm,dc=com", UniqueNameHelper.unescapeSpaces("uid=user1\\ \\ \\ \\ \\ ,dc=ibm,dc=com"));
        assertEquals("uid=user1,dc=ibm,dc=com    \\ ", UniqueNameHelper.unescapeSpaces("uid=user1,dc=ibm,dc=com\\ \\ \\ \\ \\ "));
    }
}
