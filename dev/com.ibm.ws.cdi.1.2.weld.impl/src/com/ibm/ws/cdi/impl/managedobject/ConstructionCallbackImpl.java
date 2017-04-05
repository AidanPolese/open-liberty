/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl.managedobject;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedConstructor;

import org.jboss.weld.construction.api.ConstructionHandle;

import com.ibm.ws.managedobject.ConstructionCallback;

/**
 * The implementation for ConstructionCallback
 */
public class ConstructionCallbackImpl<T> implements ConstructionCallback<T> {

    private final ConstructionHandle<T> handle;
    private final AnnotatedConstructor<T> constructor;

    public ConstructionCallbackImpl(ConstructionHandle<T> handle, AnnotatedConstructor<T> constructor) {
        this.handle = handle;
        this.constructor = constructor;
    }

    /** {@inheritDoc} */
    @Override
    public T proceed(Object[] parameters, Map<String, Object> data) {
        return handle.proceed(parameters, data);
    }

    /** {@inheritDoc} */
    @Override
    public Constructor<T> getConstructor() {
        return constructor.getJavaMember();
    }

}
