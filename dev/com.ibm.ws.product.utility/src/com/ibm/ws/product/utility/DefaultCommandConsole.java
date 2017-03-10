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
package com.ibm.ws.product.utility;

import java.io.Console;
import java.io.IOError;
import java.io.PrintStream;
import java.text.MessageFormat;

public class DefaultCommandConsole implements CommandConsole {

    private final Console console;

    private final PrintStream stderr;

    private final PrintStream stdout;

    public DefaultCommandConsole(Console console, PrintStream stdout, PrintStream stderr) {
        this.console = console;
        this.stderr = stderr;
        this.stdout = stdout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInputStreamAvailable() {
        return console != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readMaskedText(String prompt) {
        if (!isInputStreamAvailable()) {
            stderr.println(CommandConstants.PRODUCT_MESSAGES.getString("ERROR_INPUT_CONSOLE_NOT_AVAILABLE"));
            return null;
        }
        try {
            char[] in = console.readPassword(prompt);
            if (in == null) {
                return null;
            } else {
                return String.valueOf(in);
            }
        } catch (IOError e) {
            stderr.println(MessageFormat.format(CommandConstants.PRODUCT_MESSAGES.getString("ERROR_UNABLE_READ_FROM_CONSOLE"), e.getMessage()));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readText(String prompt) {
        if (!isInputStreamAvailable()) {
            stderr.println(CommandConstants.PRODUCT_MESSAGES.getString("ERROR_INPUT_CONSOLE_NOT_AVAILABLE"));
            return null;
        }
        try {
            return console.readLine(prompt);
        } catch (IOError e) {
            stderr.println(MessageFormat.format(CommandConstants.PRODUCT_MESSAGES.getString("ERROR_UNABLE_READ_FROM_CONSOLE"), e.getMessage()));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printInfoMessage(String message) {
        stdout.print(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printlnInfoMessage(String message) {
        stdout.println(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printErrorMessage(String errorMessage) {
        stderr.print(errorMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printlnErrorMessage(String errorMessage) {
        stderr.println(errorMessage);
    }
}
