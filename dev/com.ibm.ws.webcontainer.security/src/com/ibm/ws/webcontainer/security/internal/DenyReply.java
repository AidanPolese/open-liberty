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
 * Deny reply sends a 403 to deny the requested resource
 */
public class DenyReply extends WebReply {

    public DenyReply(String reason) {
        // response code 403
        super(HttpServletResponse.SC_FORBIDDEN, reason);
    }

    public void writeResponse(HttpServletResponse rsp) throws IOException {
        sendError(rsp);
    }
}
