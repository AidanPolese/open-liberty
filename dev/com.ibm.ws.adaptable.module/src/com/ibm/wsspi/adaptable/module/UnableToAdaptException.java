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
package com.ibm.wsspi.adaptable.module;

/**
 *
 */
public class UnableToAdaptException extends Exception {
    /**  */
    private static final long serialVersionUID = -3503884495911783173L;

    public UnableToAdaptException(String message) {
        super(message);
    }

    public UnableToAdaptException(Throwable t) {
        super(t);
    }

    public UnableToAdaptException(String message, Throwable t) {
        super(message, t);
    }
}
