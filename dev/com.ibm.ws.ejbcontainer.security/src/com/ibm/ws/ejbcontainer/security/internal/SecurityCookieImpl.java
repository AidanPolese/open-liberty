/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.security.internal;

import javax.security.auth.Subject;

/**
 * A place to store the subject information at the beginning of the preinvoke.
 */
public class SecurityCookieImpl {
    private final Subject invokedSubject;
    private final Subject receivedSubject;

    SecurityCookieImpl(Subject invokedSubject, Subject receivedSubject) {
        this.invokedSubject = invokedSubject;
        this.receivedSubject = receivedSubject;
    }

    public Subject getInvokedSubject() {
        return invokedSubject;
    }

    public Subject getReceivedSubject() {
        return receivedSubject;
    }
}
