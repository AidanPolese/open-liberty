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
package com.ibm.ws.microprofile.faulttolerance.test.util;

import java.lang.reflect.Method;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

/**
 *
 */
public class ExecutionContextImpl implements ExecutionContext {

    private final Method method;
    private final Object[] params;

    public ExecutionContextImpl(Object... params) {
        this(null, params);
    }

    public ExecutionContextImpl(Method method, Object... params) {
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
        StringBuilder builder = new StringBuilder("Execution Context: ");
        builder.append(method);
        builder.append(" [");
        for (Object param : params) {
            builder.append(param);
        }
        return builder.toString();
    }

}
