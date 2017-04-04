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
package com.ibm.ws.security.context.internal;

import javax.security.auth.Subject;

/**
 * The thread context that holds the caller and invocation subjects.
 */
public class SubjectThreadContext {

    private Subject callerSubject;
    private Subject invocationSubject;

    /**
     * Sets the caller subject.
     */
    public void setCallerSubject(Subject callerSubject) {
        this.callerSubject = callerSubject;
    }

    /**
     * Gets the caller subject.
     */
    public Subject getCallerSubject() {
        return callerSubject;
    }

    /**
     * Sets the invocation subject.
     */
    public void setInvocationSubject(Subject invocationSubject) {
        this.invocationSubject = invocationSubject;
    }

    /**
     * Gets the invocation subject.
     */
    public Subject getInvocationSubject() {
        return invocationSubject;
    }

    /**
     * Clears the caller and invocation subjects by setting them to null.
     */
    public void clearSubjects() {
        callerSubject = null;
        invocationSubject = null;
    }

}
