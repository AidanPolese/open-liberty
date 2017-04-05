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
package com.ibm.ws.cdi.impl.weld.injection;

import java.lang.annotation.Annotation;

import org.jboss.weld.injection.spi.ResourceReference;

import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;

/**
 *
 */
public class BindingResourceReferenceImpl<T, S extends Annotation> implements ResourceReference<T> {

    private final InjectionBinding<S> binding;

    public BindingResourceReferenceImpl(InjectionBinding<S> binding) {
        this.binding = binding;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T getInstance() {
        try {
            return (T) binding.getInjectionObject();
        } catch (InjectionException e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void release() {
        //no-op
    }

}
