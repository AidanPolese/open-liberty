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
package com.ibm.ws.jca.inbound.security;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.ws.jca.security.JCASecurityContext;
import com.ibm.ws.security.context.SubjectManager;

/**
 * Runs a work instance under the invocation subject present on the thread of execution.
 */
public class JCASecurityContextService implements JCASecurityContext {

    private final SubjectManager subjectManager = new SubjectManager();

    /** {@inheritDoc} */
    @Override
    public void runInInboundSecurityContext(final Runnable work) {
        Subject doAsSubject = subjectManager.getInvocationSubject();
        PrivilegedAction<Runnable> privEx = new PrivilegedAction<Runnable>() {
            @Override
            public Runnable run() {
                work.run();
                return null;
            }
        };
        if (doAsSubject != null) {
            WSSubject.doAs(subjectManager.getInvocationSubject(), privEx);
        } else {
            work.run();
        }
    }

}
