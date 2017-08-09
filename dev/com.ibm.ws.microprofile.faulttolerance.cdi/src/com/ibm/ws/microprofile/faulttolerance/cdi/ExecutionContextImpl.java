/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
