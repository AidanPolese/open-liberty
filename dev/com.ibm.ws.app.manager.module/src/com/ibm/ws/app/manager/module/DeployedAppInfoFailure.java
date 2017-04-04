/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.module;

/**
 * A helper class for setting an application start/stop operation result to
 * an error with a translated message.
 */
@SuppressWarnings("serial")
public class DeployedAppInfoFailure extends Exception {
    public DeployedAppInfoFailure(String translatedMessage, Throwable cause) {
        super(translatedMessage, cause);
    }

    // Override to remove the class name.
    @Override
    public String toString() {
        return getMessage();
    }
}
