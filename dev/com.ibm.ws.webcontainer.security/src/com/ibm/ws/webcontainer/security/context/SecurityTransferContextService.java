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
package com.ibm.ws.webcontainer.security.context;

import java.util.Map;

import javax.security.auth.Subject;

import com.ibm.ws.security.context.SubjectManager;
import com.ibm.wsspi.webcontainer.servlet.ITransferContextService;

/**
 * Handles security context management for threading for async servlets.
 * Right now, that's just subjects.
 */
public class SecurityTransferContextService implements ITransferContextService {
    public static final String CALLER_SUBJECT_KEY = "com.ibm.ws.webcontainer.security.internal.context.CallerSubject";
    public static final String INVOCATION_SUBJECT_KEY = "com.ibm.ws.webcontainer.security.internal.context.InvocationSubject";
    private final SubjectManager subjectManager = new SubjectManager();

    /** {@inheritDoc} */
    @Override
    public void storeState(Map<String, Object> m) {
        m.put(CALLER_SUBJECT_KEY, subjectManager.getCallerSubject());
        m.put(INVOCATION_SUBJECT_KEY, subjectManager.getInvocationSubject());
    }

    /** {@inheritDoc} */
    @Override
    public void restoreState(Map<String, Object> m) {
        subjectManager.setCallerSubject((Subject) m.get(CALLER_SUBJECT_KEY));
        subjectManager.setInvocationSubject((Subject) m.get(INVOCATION_SUBJECT_KEY));
    }

    /** {@inheritDoc} */
    @Override
    public void resetState() {
        subjectManager.clearSubjects();
    }
}
