/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.classloading;

public class ClassLoadingServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClassLoadingServiceException(String msg) {
        super(msg);
    }

    public ClassLoadingServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
