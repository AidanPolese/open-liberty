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
package com.ibm.ws.webcontainer.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApplicationErrorUtilsTest {
    @Test
    public void printTrimmedStackA() {
        Exception e = new Exception();
        e.fillInStackTrace();
        String html = ApplicationErrorUtils.getTrimmedStackHtml(e);
        // Make sure we got something back
        assertNotNull("The HTML for the stack trace shouldn't be null", html);
        assertTrue("The HTML for the stack trace should be non-trivial: " + html, html.length() > 30);
    }

    @Test
    public void printTrimmedStackForExceptionWithCause() {
        Exception e = new Exception(new NullPointerException());
        e.fillInStackTrace();
        String html = ApplicationErrorUtils.getTrimmedStackHtml(e);
        // Make sure we got something back
        assertNotNull("The HTML for the stack trace shouldn't be null", html);
        assertTrue("The HTML for the stack trace should include a Caused by clause: " + html, html.contains("Caused by"));
    }
}
