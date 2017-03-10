/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.location;

/**
 *
 */
public class SymbolException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Create an unchecked exception with the specified localized
     * message for the invalid path.
     * 
     * @param string
     *            exception message
     */
    public SymbolException(String string) {
        super(string);
    }
}
