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
package com.ibm.wsspi.kernel.service.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 */
public class LDAPUtilsTest {
    private static final String[] TEXT_FORMATS = { "%s", "%sfoo", "foo%s", "foo%sbar" };

    private static void assertEscapeFilter(String reason, String expected, String input) {
        String actual = LDAPUtils.escapeLDAPFilterTerm(input);
        assertEquals(reason, expected, actual);
    }

    private static void testEscapeFilterChar(char c, String expectedEscape) {
        final String reason = "'" + c + "' should be escaped to \"" + expectedEscape + "\"";
        for (String format : TEXT_FORMATS) {
            String expected = String.format(format, expectedEscape);
            String input = String.format(format, c);
            assertEscapeFilter(reason, expected, input);
        }
    }

    @Test
    public void testEscapeFilterTerm() {
        assertEscapeFilter("null string should not be escaped", null, null);
        assertEscapeFilter("empty string should not be escaped", "", "");
        testEscapeFilterChar('a', "a");
        testEscapeFilterChar('*', "\\2a");
        testEscapeFilterChar('(', "\\28");
        testEscapeFilterChar(')', "\\29");
        testEscapeFilterChar('\\', "\\5c");
        testEscapeFilterChar('\u00a3', "\\c2\\a3"); // the pound sign
    }

}
