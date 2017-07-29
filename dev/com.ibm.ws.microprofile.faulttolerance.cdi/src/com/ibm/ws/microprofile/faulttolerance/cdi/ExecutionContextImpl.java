/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance.cdi;

import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

/**
 *
 */
public class ExecutionContextImpl implements ExecutionContext {

    private final Method method;
    private final Object[] params;

    /**
     * @param invocationContext
     */
    public ExecutionContextImpl(InvocationContext invocationContext) {
        this(invocationContext.getMethod(), invocationContext.getParameters());
    }

    public ExecutionContextImpl(Method method, Object[] params) {
        this.method = method;
        this.params = new Object[params.length];
        //TODO is an arraycopy really required here?
        System.arraycopy(params, 0, this.params, 0, params.length);
    }

    /** {@inheritDoc} */
    @Override
    public Method getMethod() {
        return method;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getParameters() {
        return params;
    }

    @Override
    public String toString() {
        return "Execution Context: " + method;
    }

}
