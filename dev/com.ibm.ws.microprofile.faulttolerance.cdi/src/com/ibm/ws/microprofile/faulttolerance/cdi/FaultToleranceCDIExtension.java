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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.extension.WebSphereCDIExtension;

@Component(service = WebSphereCDIExtension.class, immediate = true)
public class FaultToleranceCDIExtension implements Extension, WebSphereCDIExtension {

    private static final TraceComponent tc = Tr.register(FaultToleranceCDIExtension.class);

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {
        //register the interceptor binding and in the interceptor itself
        AnnotatedType<FaultTolerance> bindingType = beanManager.createAnnotatedType(FaultTolerance.class);
        beforeBeanDiscovery.addInterceptorBinding(bindingType);
        AnnotatedType<FaultToleranceInterceptor> interceptorType = beanManager.createAnnotatedType(FaultToleranceInterceptor.class);
        beforeBeanDiscovery.addAnnotatedType(interceptorType);
    }

    public <T> void processAnnotatedType(@Observes @WithAnnotations({ Asynchronous.class, Fallback.class, Timeout.class, CircuitBreaker.class, Retry.class,
                                                                      Bulkhead.class }) ProcessAnnotatedType<T> processAnnotatedType,
                                         BeanManager beanManager) {

        Set<AnnotatedMethod<?>> interceptedMethods = new HashSet<AnnotatedMethod<?>>();
        boolean interceptedClass = false;
        boolean classLevelAsync = false;

        AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
        //get the target class
        Class<?> clazz = processAnnotatedType.getClass();
        //look at the class level annotations
        Set<Annotation> annotations = annotatedType.getAnnotations();
        for (Annotation annotation : annotations) {
            //if we find any of the fault tolerance annotations on the class then we will add the intereceptor binding to the class
            if (FTAnnotationUtils.ANNOTATIONS.contains(annotation.annotationType())) {
                interceptedClass = true;
                if (annotation.annotationType() == Asynchronous.class) {
                    classLevelAsync = true;
                } else if (annotation.annotationType() == Retry.class) {
                    PolicyValidationUtils.validateRetry(clazz, null, (Retry) annotation);
                } else if (annotation.annotationType() == Timeout.class) {
                    PolicyValidationUtils.validateTimeout(clazz, null, (Timeout) annotation);
                } else if (annotation.annotationType() == CircuitBreaker.class) {
                    PolicyValidationUtils.validateCircuitBreaker(clazz, null, (CircuitBreaker) annotation);
                } else if (annotation.annotationType() == Bulkhead.class) {
                    PolicyValidationUtils.validateBulkhead(clazz, null, (Bulkhead) annotation);
                }

            }

        }

        //now loop through the methods
        Set<AnnotatedMethod<? super T>> methods = annotatedType.getMethods();
        for (AnnotatedMethod<?> method : methods) {
            Method originalMethod = method.getJavaMember();
            if (classLevelAsync) {
                PolicyValidationUtils.validateAsynchronous(originalMethod);
            }
            annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (FTAnnotationUtils.ANNOTATIONS.contains(annotation.annotationType())) {
                    if (annotation.annotationType() == Asynchronous.class) {
                        PolicyValidationUtils.validateAsynchronous(originalMethod);

                    } else if (annotation.annotationType() == Fallback.class) {
                        PolicyValidationUtils.validateFallback(originalMethod, (Fallback) annotation);
                    } else if (annotation.annotationType() == Retry.class) {
                        PolicyValidationUtils.validateRetry(clazz, originalMethod, (Retry) annotation);
                    } else if (annotation.annotationType() == Timeout.class) {
                        PolicyValidationUtils.validateTimeout(clazz, originalMethod, (Timeout) annotation);

                    } else if (annotation.annotationType() == CircuitBreaker.class) {
                        PolicyValidationUtils.validateCircuitBreaker(clazz, originalMethod, (CircuitBreaker) annotation);
                    } else if (annotation.annotationType() == Bulkhead.class) {
                        PolicyValidationUtils.validateBulkhead(clazz, originalMethod, (Bulkhead) annotation);
                    }
                    interceptedMethods.add(method);
                }
            }
        }

        //if there were any FT annotations on the class or methods then add the interceptor binding to the methods
        if (interceptedClass || !interceptedMethods.isEmpty())

        {
            addFaultToleranceAnnotation(beanManager, processAnnotatedType, interceptedClass, interceptedMethods);
        }
    }

    private <T> void addFaultToleranceAnnotation(BeanManager beanManager, ProcessAnnotatedType<T> processAnnotatedType, boolean interceptedClass,
                                                 Set<AnnotatedMethod<?>> interceptedMethods) {
        AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(beanManager, processAnnotatedType.getAnnotatedType(), interceptedClass, interceptedMethods);
        processAnnotatedType.setAnnotatedType(wrapper);
    }
}
