/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.utility.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ibm.ws.security.utility.utils.ConsoleWrapper;

/**
 * This has a limited amount of tests because we can not mock System.console.
 */
public class ConsoleWrapperTest {

    /**
     * Test method for {@link com.ibm.ws.security.utility.utils.ConsoleWrapper#isInputStreamAvailable()}.
     */
    @Test
    public void isConsoleAvailable_false() {
        ConsoleWrapper console = new ConsoleWrapper(null, null);
        assertFalse("Console should not be available when null",
                    console.isInputStreamAvailable());
    }

    /**
     * Test method for {@link com.ibm.ws.security.utility.utils.ConsoleWrapper#readMaskedText(java.lang.String)}.
     */
    @Test
    public void readMaskedText_noConsole() {
        ConsoleWrapper console = new ConsoleWrapper(null, System.err);
        assertNull(console.readMaskedText("My prompt"));
    }

}
