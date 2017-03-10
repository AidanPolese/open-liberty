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

import java.io.PrintStream;

public class CommandConsoleFacade implements CommandConsole {

    private volatile PrintStream outputStream;

    private final CommandConsole delegatedConsole;

    public CommandConsoleFacade(CommandConsole delegatedConsole) {
        this.delegatedConsole = delegatedConsole;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInputStreamAvailable() {
        return delegatedConsole.isInputStreamAvailable();
    }

    /** {@inheritDoc} */
    @Override
    public String readMaskedText(String prompt) {
        return delegatedConsole.readMaskedText(prompt);
    }

    /** {@inheritDoc} */
    @Override
    public String readText(String prompt) {
        return delegatedConsole.readText(prompt);
    }

    /** {@inheritDoc} */
    @Override
    public void printInfoMessage(String message) {
        if (outputStream != null) {
            outputStream.print(message);
        } else {
            delegatedConsole.printInfoMessage(message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void printlnInfoMessage(String message) {
        if (outputStream != null) {
            outputStream.println(message);
        } else {
            delegatedConsole.printlnInfoMessage(message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void printErrorMessage(String errorMessage) {
        delegatedConsole.printErrorMessage(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void printlnErrorMessage(String errorMessage) {
        delegatedConsole.printlnErrorMessage(errorMessage);
    }

    public void setOverrideOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}
