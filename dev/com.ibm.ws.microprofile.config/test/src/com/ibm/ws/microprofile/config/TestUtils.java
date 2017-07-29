/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config;

import static org.junit.Assert.fail;

public class TestUtils {

    public static void assertContains(Iterable<String> iterable, String value) {
        StringBuilder strb = new StringBuilder();
        boolean first = true;
        for (String str : iterable) {
            if (str.equals(value)) {
                return;
            } else {
                if (!first) {
                    strb.append(", ");
                } else {
                    first = false;
                }
                strb.append(str);
            }
        }
        fail("Iterable (" + strb + ") did not contain: " + value);
    }

    public static void assertNotContains(Iterable<String> iterable, String value) {
        StringBuilder strb = new StringBuilder();
        boolean contains = false;
        boolean first = true;
        for (String str : iterable) {
            if (str.equals(value)) {
                contains = true;
            }

            if (!first) {
                strb.append(", ");
            } else {
                first = false;
            }
            strb.append(str);
        }
        if (contains) {
            fail("Iterable (" + strb + ") DID contain: " + value);
        }
    }
}
