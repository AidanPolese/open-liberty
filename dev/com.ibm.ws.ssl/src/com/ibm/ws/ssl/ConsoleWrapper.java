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
package com.ibm.ws.ssl;

import java.io.Console;
import java.io.IOError;
import java.io.PrintStream;

/**
 * Wraps the standard input console, and allows us to handle char[] to
 * String conversion and exception logic. This also allows us to mock
 * up the console for unit test purposes.
 */
public class ConsoleWrapper {
    private final Console console;
    private final PrintStream stderr;

    public ConsoleWrapper(Console console, PrintStream stderr) {
        this.console = console;
        this.stderr = stderr;
    }

    /**
     * Determines if the Console is currently available.
     * 
     * @return true if the Console is available, false otherwise.
     */
    public boolean isInputStreamAvailable() {
        return console != null;
    }

    /**
     * Reads text from the input String, prompting with the given String.
     * 
     * @param prompt
     * @return String read from input.
     */
    public String readText(String prompt) {
        if (!isInputStreamAvailable()) {
            return null;
        }
        try {
            return console.readLine(prompt);
        } catch (IOError e) {
            stderr.println("Exception while reading stdin: " + e.getMessage());
            e.printStackTrace(stderr);
        }
        return null;
    }

}
