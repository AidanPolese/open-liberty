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
package org.apache.felix.http.base.internal.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 */
public class ServletHandlerTest {

    /**
     * Test method for {@link org.apache.felix.http.base.internal.handler.ServletHandler#hashCode()}.
     */
    @Test
    public void test_hashCode() {
        ServletHandler handler1 = new ServletHandler(null, null, "alias1");
        ServletHandler handler2 = new ServletHandler(null, null, "alias1");
        ServletHandler handler3 = new ServletHandler(null, null, "alias2");

        assertTrue("FAIL: hash codes were not equal when they should be",
                     handler1.hashCode() == handler2.hashCode());
        assertFalse("FAIL: hash codes were equal when they should not be",
                      handler1.hashCode() == handler3.hashCode());
    }

    /**
     * Test method for {@link org.apache.felix.http.base.internal.handler.ServletHandler#compareTo(org.apache.felix.http.base.internal.handler.ServletHandler)}.
     */
    @Test
    public void test_compareTo() {
        ServletHandler handler1 = new ServletHandler(null, null, "alias1");
        ServletHandler handler2 = new ServletHandler(null, null, "alias1");
        ServletHandler handler3 = new ServletHandler(null, null, "alias2");

        assertEquals("FAIL: compareTo should have returned 0 for equal objects",
                     0, handler1.compareTo(handler2));
        assertEquals("FAIL: compareTo should have returned 0 for equal objects",
                     0, handler2.compareTo(handler1));
        assertTrue("FAIL: compareTo should not have returned 0 for unequal objects",
                      handler1.compareTo(handler3) != 0);
        assertTrue("FAIL: compareTo should not have returned 0 for unequal objects",
                   handler3.compareTo(handler1) != 0);
    }

    /**
     * Test method for {@link org.apache.felix.http.base.internal.handler.ServletHandler#equals(java.lang.Object)}.
     */
    @Test
    public void test_equalsObject() {
        ServletHandler handler1 = new ServletHandler(null, null, "alias1");
        ServletHandler handler2 = new ServletHandler(null, null, "alias1");
        ServletHandler handler3 = new ServletHandler(null, null, "alias2");

        assertEquals("FAIL: objects were not equal when they should be",
                     handler1, handler2);
        assertFalse("FAIL: objects were equal when they should not be",
                      handler1.equals(handler3));
        assertFalse("FAIL: objects were equal when they should not be",
                    handler1.equals("Wrong Type"));
    }

}
