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
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedMethodWrapper<T> extends AnnotatedWrapper implements AnnotatedMethod<T> {

    private final AnnotatedMethod<T> wrapped;
    private final AnnotatedType<T> declaringType;

    public AnnotatedMethodWrapper(AnnotatedType<T> declaringType, AnnotatedMethod<T> wrapped) {
        super(wrapped, true);
        this.declaringType = declaringType;
        this.wrapped = wrapped;
    }

    /** {@inheritDoc} */
    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return wrapped.getParameters();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStatic() {
        return wrapped.isStatic();
    }

    /** {@inheritDoc} */
    @Override
    public AnnotatedType<T> getDeclaringType() {
        return declaringType;
    }

    /** {@inheritDoc} */
    @Override
    public Method getJavaMember() {
        return wrapped.getJavaMember();
    }

}
