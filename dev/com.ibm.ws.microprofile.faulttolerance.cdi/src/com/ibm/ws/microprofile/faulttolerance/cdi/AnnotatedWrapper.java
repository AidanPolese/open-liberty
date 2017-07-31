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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

public class AnnotatedWrapper implements Annotated {

    private final Annotated wrapped;
    private final Set<Annotation> annotations;

    public AnnotatedWrapper(Annotated wrapped, boolean addFaultToleranceAnnotation) {
        this.wrapped = wrapped;
        this.annotations = new HashSet<>(wrapped.getAnnotations());
        if (addFaultToleranceAnnotation) {
            this.annotations.add(new FaultToleranceAnnotation());
        }
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return (A) annotation;
            }
        }
        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Type getBaseType() {
        return wrapped.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return wrapped.getTypeClosure();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }
}
