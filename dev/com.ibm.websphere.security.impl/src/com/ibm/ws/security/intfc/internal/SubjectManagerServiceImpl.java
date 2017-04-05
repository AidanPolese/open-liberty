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
package com.ibm.ws.security.intfc.internal;

import javax.security.auth.Subject;

import org.osgi.service.component.ComponentContext;

import com.ibm.ws.security.context.SubjectManager;
import com.ibm.ws.security.intfc.SubjectManagerService;

/**
 *
 */
public class SubjectManagerServiceImpl implements SubjectManagerService {

    protected void activate(ComponentContext cc) {}

    protected void deactivate(ComponentContext cc) {}

    /** {@inheritDoc} */
    @Override
    public void setCallerSubject(Subject callerSubject) {
        SubjectManager sm = new SubjectManager();
        sm.setCallerSubject(callerSubject);
    }

    /** {@inheritDoc} */
    @Override
    public Subject getCallerSubject() {
        SubjectManager sm = new SubjectManager();
        return sm.getCallerSubject();
    }

    /** {@inheritDoc} */
    @Override
    public void setInvocationSubject(Subject invocationSubject) {
        SubjectManager sm = new SubjectManager();
        sm.setInvocationSubject(invocationSubject);
    }

    /** {@inheritDoc} */
    @Override
    public Subject getInvocationSubject() {
        SubjectManager sm = new SubjectManager();
        return sm.getInvocationSubject();
    }

}
