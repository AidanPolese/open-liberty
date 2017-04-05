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

public class WebSecurityCollaboratorException extends Exception {
    private static final long serialVersionUID = 4879760322028996722L;

    private transient final WebReply wReply;

    private transient Object wSecurityContext;

    public WebSecurityCollaboratorException(WebReply reply) {
        this(null, reply, null);
    }

    public WebSecurityCollaboratorException(String msg, WebReply reply) {
        this(msg, reply, null);
    }

    public WebSecurityCollaboratorException(String msg, WebReply reply, Object securityContext) {
        super(msg);
        wReply = reply;
        wSecurityContext = securityContext;
    }

    public WebReply getWebReply() {
        return wReply;
    }

    public Object getWebSecurityContext() {
        return wSecurityContext;
    }
}
