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
package com.ibm.ws.webcontainer.security;

import javax.security.auth.Subject;

public class WebSecurityContext {

    private Subject invokedSubject = null;
    private Subject receivedSubject = null;

    /**
     * This is the token returned by a request to sync the app's invocation subject
     * identity to the thread, via ThreadIdentityManager.set(). It represents the
     * previously sync'ed identity, and must be passed back to ThreadIdentityManager.reset(token),
     * to restore the previously sync'ed identity.
     */
    private Object syncToOSThreadToken = null;
    private Object jaspiAuthContext = null;

    public WebSecurityContext(Subject iSubject, Subject rSubject) {
        invokedSubject = iSubject;
        receivedSubject = rSubject;
    }

    public Subject getInvokedSubject() {
        return invokedSubject;
    }

    public Subject getReceivedSubject() {
        return receivedSubject;
    }

    public void setSyncToOSThreadToken(Object obj) {
        syncToOSThreadToken = obj;
    }

    public Object getSyncToOSThreadToken() {
        return syncToOSThreadToken;
    }

    /**
     * @return the JASPI auth context
     */
    public Object getJaspiAuthContext() {
        return jaspiAuthContext;
    }

    /**
     * Set the JASPI auth context
     * 
     * @param jaspiAuthContext
     */
    public void setJaspiAuthContext(Object jaspiAuthContext) {
        this.jaspiAuthContext = jaspiAuthContext;
    }

}
