/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.state;

public class StateChangeException extends Exception {

    private static final long serialVersionUID = 7229330995297804384L;

    public StateChangeException(String s) {
        super(s);
    }

    public StateChangeException(String s, Throwable t) {
        super(s, t);
    }

    public StateChangeException(Throwable t) {
        super(t);
    }
}
