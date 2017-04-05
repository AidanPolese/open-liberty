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
package com.ibm.ws.webcontainer.security.internal;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Base class for all replies to a web authorization request.
 */
public abstract class WebReply {
    protected int responseCode;
    public String message = null;

    protected WebReply(int code, String msg) {
        responseCode = code;
        message = msg;
    }

    protected WebReply(int code) {
        this(code, null);
    }

    public int getStatusCode() {
        return responseCode;
    }

    public abstract void writeResponse(HttpServletResponse rsp)
                    throws IOException;

    public void sendError(HttpServletResponse rsp) throws IOException {
        rsp.sendError(responseCode, message);
    }
}
