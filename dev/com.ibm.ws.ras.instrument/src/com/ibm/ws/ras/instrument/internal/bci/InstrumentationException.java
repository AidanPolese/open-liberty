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
package com.ibm.ws.ras.instrument.internal.bci;

/**
 * Thrown by an adapter when an instrumentation fails due to programmer error.
 */
@SuppressWarnings("serial")
public class InstrumentationException extends RuntimeException {
    public InstrumentationException(String message) {
        super(message);
    }
}
