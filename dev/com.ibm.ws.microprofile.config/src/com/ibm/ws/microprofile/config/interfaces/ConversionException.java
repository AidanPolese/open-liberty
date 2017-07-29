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

package com.ibm.ws.microprofile.config.interfaces;

public class ConversionException extends ConfigException {

    /**
     * The exception was thrown when unable to convert to the specified type.
     */
    private static final long serialVersionUID = 1L;

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable throwable) {
        super(throwable);
    }

    public ConversionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
