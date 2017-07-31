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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

public class AnnotatedTypeWrapper<T> extends AnnotatedWrapper implements AnnotatedType<T> {

    private final AnnotatedType<T> wrapped;
    private final Set<AnnotatedMethod<? super T>> methods = new HashSet<>();
    private final Map<AnnotatedMethod<? super T>, AnnotatedMethodWrapper<? super T>> wrappedMethods = new HashMap<>();

    public AnnotatedTypeWrapper(BeanManager beanManager, AnnotatedType<T> wrapped, boolean interceptedClass, Set<AnnotatedMethod<?>> interceptedMethods) {
        super(wrapped, interceptedClass);
        this.wrapped = wrapped;

        for (AnnotatedMethod<? super T> method : this.wrapped.getMethods()) {
            if (interceptedMethods.contains(method)) {
                AnnotatedType<?> declaringType = method.getDeclaringType();
                if (declaringType.equals(wrapped)) {
                    AnnotatedMethodWrapper<T> methodWrapper = new AnnotatedMethodWrapper<T>(this, (AnnotatedMethod<T>) method);
                    this.methods.add(methodWrapper);
                    this.wrappedMethods.put(method, methodWrapper);
                } else {
                    throw new RuntimeException("EPIC FAIL!");
                }
            } else {
                this.methods.add(method);
            }
        }
    }

    private <X> AnnotatedTypeWrapper<X> newAnnotatedTypeWrapper(BeanManager beanManager, AnnotatedType<X> wrapped, Set<AnnotatedMethod<?>> interceptedMethods) {
        AnnotatedTypeWrapper<X> superTypeWrapper = new AnnotatedTypeWrapper<X>(beanManager, wrapped, false, interceptedMethods);
        return superTypeWrapper;
    }

    @Override
    public Set<AnnotatedConstructor<T>> getConstructors() {
        return wrapped.getConstructors();
    }

    @Override
    public Set<AnnotatedField<? super T>> getFields() {
        return wrapped.getFields();
    }

    @Override
    public Class<T> getJavaClass() {
        return wrapped.getJavaClass();
    }

    @Override
    public Set<AnnotatedMethod<? super T>> getMethods() {
        return methods;
    }

}
