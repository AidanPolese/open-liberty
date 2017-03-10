/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install;

/**
 *
 */
public class CancelException extends InstallException {

    private static final long serialVersionUID = -487184439522887816L;

    /**
     * @param message
     * @param rc
     */
    public CancelException(String message, int rc) {
        super(message, rc);
    }

}
