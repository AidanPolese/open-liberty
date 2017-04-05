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

import com.ibm.ws.exception.WsRuntimeException;

public class CDIRuntimeException extends WsRuntimeException {

    private static final long serialVersionUID = 5729749912023008025L;

    public CDIRuntimeException(String message) {
        super(message);
    }

    public CDIRuntimeException(Throwable t) {
        super(t);
    }

    public CDIRuntimeException(String message, Throwable t) {
        super(message, t);
    }

}
