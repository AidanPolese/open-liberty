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

package com.ibm.ws.ffdc;

public class DiagnosticModuleRegistrationFailureException extends Exception {
    private static final long serialVersionUID = -272181404821808015L;

    public DiagnosticModuleRegistrationFailureException() {
        super();
    }

    public DiagnosticModuleRegistrationFailureException(String s) {
        super(s);
    }

    public DiagnosticModuleRegistrationFailureException(String s, Throwable cause) {
        super(s, cause);
    }
}