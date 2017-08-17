/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.microprofile.faulttolerance_fat.util;

/**
 *
 */
public class CustomException extends Exception {
    public CustomException() {
        super();
        System.out.println("CustomException instantiated");
    }

    /**
     * @param failure
     */
    public CustomException(Throwable failure) {
        System.out.println("CustomException with failure: " + failure);
    }
}
