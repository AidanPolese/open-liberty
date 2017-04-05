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
package com.ibm.ws.cdi;

import com.ibm.ws.exception.WsException;

public class CDIException extends WsException {

    private static final long serialVersionUID = 5729749912023008025L;

    public CDIException(String message) {
        super(message);
    }

    public CDIException(Throwable t) {
        super(t);
    }

    public CDIException(String message, Throwable t) {
        super(message, t);
    }

}
