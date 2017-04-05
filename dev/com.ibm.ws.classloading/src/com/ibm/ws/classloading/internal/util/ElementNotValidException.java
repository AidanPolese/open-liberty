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

public class ElementNotValidException extends ElementNotFetchedException {
    private static final long serialVersionUID = 1L;

    public ElementNotValidException() {
        super();
    }

    public ElementNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementNotValidException(String message) {
        super(message);
    }

    public ElementNotValidException(Throwable cause) {
        super(cause);
    }
}
