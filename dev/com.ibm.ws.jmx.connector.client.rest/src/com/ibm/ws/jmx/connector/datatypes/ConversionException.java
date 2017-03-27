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
package com.ibm.ws.jmx.connector.datatypes;

public final class ConversionException extends Exception {
    private static final long serialVersionUID = -2548273252032545919L;

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable t) {
        super(message, t);
    }
}
