/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jndi.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.ibm.ws.jndi.WSName;

@SuppressWarnings("unchecked")
class Assert extends org.junit.Assert {
    private static final String EMPTY_STRING = "";

    private static String getChildren(ContextNode node) throws Exception {
        final Field f = ContextNode.class.getDeclaredField("children");
        f.setAccessible(true);
        Set<String> children = ((Map<String, Object>) f.get(node)).keySet();
        return join(sorted(children));
    }

    static void assertEquals(String msg, String expected, WSName actual) {
        String actualStr = actual == null ? null : actual.toString();
        assertEquals(msg, expected, actualStr);
    }

    static void assertEquals(String msg, String expected, ContextNode actual) {
        String actualStr = actual == null ? null : actual.toString();
        assertEquals(msg, expected, actualStr);
    }

    static void assertChildren(String expectedChildren, Object node) throws Exception {
        ContextNode wsNode = (ContextNode) node;
        String nodeName = wsNode.fullName.isEmpty() ? "root context" : "context at " + wsNode.fullName;
        String message = "The context at " + nodeName + " should have children " + expectedChildren;
        assertEquals(message, expectedChildren, getChildren(wsNode));
    }

    //////////////////////////////////
    // String and collection utils //
    ////////////////////////////////

    private static <T extends Comparable<T>> Collection<T> sorted(Set<T> keySet) {
        ArrayList<T> result = new ArrayList<T>(keySet);
        Collections.sort(result);
        return result;
    }

    private static String join(Collection<?> elems) {
        return join(elems, ",");
    }

    private static String join(Collection<?> elems, String delimiter) {
        String result = EMPTY_STRING;
        for (Object e : elems)
            result = result == EMPTY_STRING ? "" + e : result + delimiter + e;
        return result;
    }
}
