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
 * On a successful authentication and authorization, the WebCollaborator will
 * result in permitting the request to go through. Setting up credential (used
 * in AppServers), cookies (for single sign-on), auth type and remote user (for
 * Servlet API requirements) are all encapsulated within the PermitReply.
 */
public class PermitReply extends WebReply {

    public PermitReply() {
        super(HttpServletResponse.SC_OK, "OK");
    }

    @Override
    public void writeResponse(HttpServletResponse rsp) throws IOException {}
}
