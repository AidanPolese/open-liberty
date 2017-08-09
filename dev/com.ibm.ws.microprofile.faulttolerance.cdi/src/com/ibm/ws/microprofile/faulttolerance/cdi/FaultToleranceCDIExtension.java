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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.cdi.extension.WebSphereCDIExtension;

@Component(service = WebSphereCDIExtension.class, immediate = true)
public class FaultToleranceCDIExtension implements Extension, WebSphereCDIExtension {

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery beforeBeanDiscovery, BeanManager beanManager) {
        //register the interceptor binding and in the interceptor itself
        AnnotatedType<FaultTolerance> bindingType = beanManager.createAnnotatedType(FaultTolerance.class);
        beforeBeanDiscovery.addInterceptorBinding(bindingType);
        AnnotatedType<FaultToleranceInterceptor> interceptorType = beanManager.createAnnotatedType(FaultToleranceInterceptor.class);
        beforeBeanDiscovery.addAnnotatedType(interceptorType);
    }

    public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType, BeanManager beanManager) {
        Set<AnnotatedMethod<?>> interceptedMethods = new HashSet<AnnotatedMethod<?>>();
        boolean interceptedClass = false;
        boolean classLevelAsync = false;

        AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
        //look at the class level annotations
        Set<Annotation> annotations = annotatedType.getAnnotations();
        for (Annotation annotation : annotations) {
            //if we find any of the fault tolerance annotations on the class then we will add the intereceptor binding to the class
            if (FTAnnotationUtils.ANNOTATIONS.contains(annotation.annotationType())) {
                interceptedClass = true;
                if (annotation.annotationType() == Asynchronous.class) {
                    classLevelAsync = true;
                }
            }
        }

        //now loop through the methods
        Set<AnnotatedMethod<? super T>> methods = annotatedType.getMethods();
        for (AnnotatedMethod<?> method : methods) {
            Class<?> returnType = method.getJavaMember().getReturnType();
            if (classLevelAsync) {
                if (!(Future.class.isAssignableFrom(returnType))) {
                    //TODO NLS
                    throw new FaultToleranceException("@Asynchronous methods must return a Future: " + method);
                }
            }
            annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (FTAnnotationUtils.ANNOTATIONS.contains(annotation.annotationType())) {
                    if (annotation.annotationType() == Asynchronous.class) {
                        if (!(Future.class.isAssignableFrom(returnType))) {
                            //TODO NLS
                            throw new FaultToleranceException("@Asynchronous methods must return a Future: " + method);
                        }
                    }
                    interceptedMethods.add(method);
                }
            }
        }

        //if there were any FT annotations on the class or methods then add the interceptor binding to the methods
        if (interceptedClass || !interceptedMethods.isEmpty()) {
            addFaultToleranceAnnotation(beanManager, processAnnotatedType, interceptedClass, interceptedMethods);
        }
    }

    private <T> void addFaultToleranceAnnotation(BeanManager beanManager, ProcessAnnotatedType<T> processAnnotatedType, boolean interceptedClass,
                                                 Set<AnnotatedMethod<?>> interceptedMethods) {
        AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(beanManager, processAnnotatedType.getAnnotatedType(), interceptedClass, interceptedMethods);
        processAnnotatedType.setAnnotatedType(wrapper);
    }
}
