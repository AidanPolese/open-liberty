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
package com.ibm.ws.timedexit.internal;

public class RequiredTimedExitFeatureStoppedException extends Exception {

    private static final long serialVersionUID = 1L;

    public RequiredTimedExitFeatureStoppedException(String message) {
        super(message);
    }
}
