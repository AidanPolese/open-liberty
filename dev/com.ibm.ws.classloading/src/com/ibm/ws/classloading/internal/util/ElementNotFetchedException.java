/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal.util;

public abstract class ElementNotFetchedException extends Exception {
    private static final long serialVersionUID = 1L;

    public ElementNotFetchedException() {}

    public ElementNotFetchedException(String message) {
        super(message);
    }

    public ElementNotFetchedException(Throwable cause) {
        super(cause);
    }

    public ElementNotFetchedException(String message, Throwable cause) {
        super(message, cause);
    }

}
