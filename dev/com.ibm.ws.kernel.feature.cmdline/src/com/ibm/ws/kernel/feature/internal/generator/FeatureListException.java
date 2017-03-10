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
package com.ibm.ws.kernel.feature.internal.generator;

/**
 * This subclass of RuntimeException is used in the tool to differenciate between how to handle
 * RuntimeExceptions with nice messages that are probably user error and ones which don't have
 * pretty error messages and are probably product bugs.
 */
public class FeatureListException extends RuntimeException {

    public FeatureListException() {
        super();
    }

    public FeatureListException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureListException(String message) {
        super(message);
    }

    public FeatureListException(Throwable cause) {
        super(cause);
    }

}
