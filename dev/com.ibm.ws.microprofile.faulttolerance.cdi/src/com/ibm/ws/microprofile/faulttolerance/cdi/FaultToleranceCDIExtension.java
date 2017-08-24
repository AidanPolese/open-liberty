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
import java.util.concurrent.Future;

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
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;
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

        //validate Asynchronous
        //validate fallback

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
            Method originalMethod = method.getJavaMember();
            Class<?> originalMethodReturnType = originalMethod.getReturnType();

            if (classLevelAsync) {
                if (!(Future.class.isAssignableFrom(originalMethodReturnType))) {
                    throw new FaultToleranceException(Tr.formatMessage(tc, "asynchronous.class.not.returning.future.CWMFT5000E", method));
                }
            }
            annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (FTAnnotationUtils.ANNOTATIONS.contains(annotation.annotationType())) {
                    if (annotation.annotationType() == Asynchronous.class) {
                        if (!(Future.class.isAssignableFrom(originalMethodReturnType))) {
                            throw new FaultToleranceException(Tr.formatMessage(tc, "asynchronous.method.not.returning.future.CWMFT5001E", method));
                        }
                    } else if (annotation.annotationType() == Fallback.class) {
                        validateFallback(originalMethod, annotation);
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

    /**
     * Validate @Fallback
     * 1) The parameter value and fallbackMethod on Fallback cannot be specified at the same time.
     * Otherwise, a deployment exception will be thrown.
     * 2) Fallback method return type should be assignable to the original return type
     * 3) The return type of the only method in FallbackHandler.handle should be assignable to the original return type
     *
     * @param originalMethod
     * @param annotation
     */
    private void validateFallback(Method originalMethod, Annotation annotation) {
        //validate the fallback annotation

        Class<?> originalMethodReturnType = originalMethod.getReturnType();
        Class<?>[] originalMethodParamTypes = originalMethod.getParameterTypes();
        Fallback fb = (Fallback) annotation;
        Class<? extends FallbackHandler<?>> fallbackClass = fb.value();
        String fallbackMethodName = fb.fallbackMethod();
        //If both fallback method and fallback class are set, it is an illegal state.
        if ((fallbackClass != null && fallbackClass != Fallback.DEFAULT.class) && (fallbackMethodName != null && !"".equals(fallbackMethodName))) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.policy.conflicts.CWMFT5009E", originalMethod, fallbackClass, fallbackMethodName));
        } else if (fallbackClass != null && fallbackClass != Fallback.DEFAULT.class) {
            //TODO validate the return type
            //need to load the fallback class and then find out the method return type

            try {
                Method[] ms = fallbackClass.getMethods();
                Method handleMethod = FallbackHandler.class.getMethod(FTAnnotationUtils.FALLBACKHANDLE_METHOD_NAME, ExecutionContext.class);
                boolean validFallbackHandler = false;
                for (Method m : ms) {
                    if (m.getName().equals(handleMethod.getName()) && (m.getParameterCount() == 1)) {
                        Class<?>[] params = m.getParameterTypes();
                        if (ExecutionContext.class.isAssignableFrom(params[0])) {
                            //now check the return type
                            if (originalMethodReturnType.isAssignableFrom(m.getReturnType())) {
                                validFallbackHandler = true;
                                break;
                            }
                        }
                    }
                }

                if (!validFallbackHandler) {
                    throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.policy.invalid.CWMFT5008E", originalMethod, fallbackClass, originalMethodReturnType,
                                                                       originalMethod));
                }
            } catch (NoSuchMethodException e) {
                //should not happen
                throw new FaultToleranceException(Tr.formatMessage(tc, "internal.error.CWMFT5998E"), e);
            } catch (SecurityException e) {
                //should not happen
                throw new FaultToleranceException((Tr.formatMessage(tc, "internal.error.CWMFT5998E")), e);
            }

        } else if (fallbackMethodName != null && !"".equals(fallbackMethodName)) {

            try {

                Method fallbackMethod = originalMethod.getDeclaringClass().getMethod(fallbackMethodName, originalMethodParamTypes);

                Class<?> fallbackReturn = fallbackMethod.getReturnType();
                if (!originalMethodReturnType.isAssignableFrom(fallbackReturn)) {
                    throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.policy.return.type.not.match.CWMFT5002E", fallbackMethod, originalMethod));
                }

                //validate the args matching as well

            } catch (NoSuchMethodException e) {
                throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.method.not.found.CWMFT5003E", fallbackMethodName,
                                                                   originalMethod.getName(), originalMethod.getDeclaringClass()), e);
            } catch (SecurityException e) {
                throw new FaultToleranceException((Tr.formatMessage(tc, "security.exception.acquiring.fallback.method.CWMFT5004E")), e);
            }

        }
    }

    private <T> void addFaultToleranceAnnotation(BeanManager beanManager, ProcessAnnotatedType<T> processAnnotatedType, boolean interceptedClass,
                                                 Set<AnnotatedMethod<?>> interceptedMethods) {
        AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(beanManager, processAnnotatedType.getAnnotatedType(), interceptedClass, interceptedMethods);
        processAnnotatedType.setAnnotatedType(wrapper);
    }
}
