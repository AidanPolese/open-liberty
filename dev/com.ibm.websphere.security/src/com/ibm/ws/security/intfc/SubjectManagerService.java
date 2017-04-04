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
package com.ibm.ws.security.intfc;

import javax.security.auth.Subject;

/**
 * Please do not use this interface. Use WSSubject instead for all your thread
 * security context needs.
 * 
 * This interface is intended for internal only use by the security component to
 * work around visibility and circular build path issues.
 */
public interface SubjectManagerService {

    public final String KEY_SUBJECT_MANAGER_SERVICE = "subjectManagerService";

    /**
     * Sets the caller subject on the thread.
     */
    public void setCallerSubject(Subject callerSubject);

    /**
     * Gets the caller subject from the thread.
     */
    public Subject getCallerSubject();

    /**
     * Sets the invocation subject on the thread.
     */
    public void setInvocationSubject(Subject invocationSubject);

    /**
     * Gets the invocation subject from the thread.
     */
    public Subject getInvocationSubject();
}
