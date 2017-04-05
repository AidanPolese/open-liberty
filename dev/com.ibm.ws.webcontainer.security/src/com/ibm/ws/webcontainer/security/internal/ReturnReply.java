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
 * Web reply to return the response object as is, regardless of
 * HTTP status code (originally created for JASPI 1.1 forward/include)
 */
public class ReturnReply extends WebReply {

    public ReturnReply(int code, String msg) {
        super(code, msg);
    }

    /**
     * Response may be committed so no writing
     */
    @Override
    public void writeResponse(HttpServletResponse rsp) throws IOException {}
}
