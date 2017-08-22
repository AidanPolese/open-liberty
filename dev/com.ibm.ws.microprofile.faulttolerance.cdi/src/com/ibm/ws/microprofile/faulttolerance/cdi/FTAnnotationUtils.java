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
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InjectionTargetFactory;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.AsynchronousConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.BulkheadConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.CircuitBreakerConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.FallbackConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.RetryConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.TimeoutConfig;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackHandlerFactory;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

public class FTAnnotationUtils {

    private static final TraceComponent tc = Tr.register(FTAnnotationUtils.class);

    public final static Set<Class<?>> ANNOTATIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Asynchronous.class, CircuitBreaker.class,
                                                                                                            Retry.class, Timeout.class, Bulkhead.class, Fallback.class)));

    static RetryPolicy processRetryAnnotation(Retry retry) {
        int maxRetries = retry.maxRetries();

        long longDelay = retry.delay();
        Duration delay = null;
        if (longDelay > 0) {
            delay = Duration.of(longDelay, retry.delayUnit());
        }

        long longMaxDuration = retry.maxDuration();
        Duration maxDuration = null;
        if (longMaxDuration > 0) {
            maxDuration = Duration.of(longMaxDuration, retry.durationUnit());
        }

        long longJitter = retry.jitter();
        Duration jitter = null;
        if (longJitter > 0) {
            jitter = Duration.of(longJitter, retry.jitterDelayUnit());
        }

        Class<? extends Throwable>[] retryOn = retry.retryOn();
        Class<? extends Throwable>[] abortOn = retry.abortOn();

        RetryPolicy retryPolicy = FaultToleranceProvider.newRetryPolicy();

        retryPolicy.setMaxRetries(maxRetries);
        if (delay != null) {
            retryPolicy.setDelay(delay);
        }
        if (maxDuration != null) {
            retryPolicy.setMaxDuration(maxDuration);
        }
        if (jitter != null) {
            retryPolicy.setJitter(jitter);
        }
        if (retryOn != null && retryOn.length > 0) {
            retryPolicy.setRetryOn(retryOn);
        }
        if (abortOn != null && abortOn.length > 0) {
            retryPolicy.setAbortOn(abortOn);
        }

        return retryPolicy;
    }

    static CircuitBreakerPolicy processCircuitBreakerAnnotation(CircuitBreaker circuitBreaker) {
        Class<? extends Throwable>[] failOn = circuitBreaker.failOn();

        long longDelay = circuitBreaker.delay();
        Duration delay = null;
        if (longDelay > 0) {
            delay = Duration.of(longDelay, circuitBreaker.delayUnit());
        }

        //TODO validation
        int requestVolumeThreshold = circuitBreaker.requestVolumeThreshold();
        double failureRatio = circuitBreaker.failureRatio();
        int successThreshold = circuitBreaker.successThreshold();

        CircuitBreakerPolicy circuitBreakerPolicy = FaultToleranceProvider.newCircuitBreakerPolicy();

        if (failOn != null && failOn.length > 0) {
            circuitBreakerPolicy.setFailOn(failOn);
        }

        if (delay != null) {
            circuitBreakerPolicy.setDelay(delay);
        }

        circuitBreakerPolicy.setRequestVolumeThreshold(requestVolumeThreshold);
        circuitBreakerPolicy.setFailureRatio(failureRatio);

        circuitBreakerPolicy.setSuccessThreshold(successThreshold);

        return circuitBreakerPolicy;
    }

    static BulkheadPolicy processBulkheadAnnotation(Bulkhead bulkhead) {
        int maxThreads = bulkhead.value();
        int queueSize = bulkhead.waitingTaskQueue();

        BulkheadPolicy bulkheadPolicy = FaultToleranceProvider.newBulkheadPolicy();

        bulkheadPolicy.setMaxThreads(maxThreads);
        bulkheadPolicy.setQueueSize(queueSize);

        return bulkheadPolicy;
    }

    static FallbackPolicy processFallbackAnnotation(Fallback fallback, InvocationContext context, BeanManager beanManager) {
        FallbackPolicy fallbackPolicy = null;
        Class<? extends FallbackHandler<?>> fallbackClass = fallback.value();
        String fallbackMethodName = fallback.fallbackMethod();
        //If both fallback method and fallback class are set, it is an illegal state.
        if ((fallbackClass != null && fallbackClass != Fallback.DEFAULT.class) && (fallbackMethodName != null && !"".equals(fallbackMethodName))) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.policy.conflicts.CWMFT5008E", context.getMethod(), fallbackClass, fallbackMethodName));
        } else if (fallbackClass != null && fallbackClass != Fallback.DEFAULT.class) {
            FallbackHandlerFactory fallbackHandlerFactory = getFallbackHandlerFactory(beanManager);
            fallbackPolicy = newFallbackPolicy(fallbackClass, fallbackHandlerFactory);
        } else if (fallbackMethodName != null && !"".equals(fallbackMethodName)) {
            Object beanInstance = context.getTarget();
            Method originalMethod = context.getMethod();
            Class<?>[] paramTypes = originalMethod.getParameterTypes();
            try {
                Method fallbackMethod = beanInstance.getClass().getMethod(fallbackMethodName, paramTypes);
                Class<?> originalReturn = originalMethod.getReturnType();
                Class<?> fallbackReturn = fallbackMethod.getReturnType();
                if (originalReturn.isAssignableFrom(fallbackReturn)) {
                    fallbackPolicy = newFallbackPolicy(beanInstance, fallbackMethod, fallbackReturn);
                } else {
                    throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.policy.return.type.not.match.CWMFT5002E", fallbackMethod, originalMethod));
                }
            } catch (NoSuchMethodException e) {
                throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.method.not.found.CWMFT5003E", beanInstance.getClass(), fallbackMethodName, paramTypes), e);
            } catch (SecurityException e) {
                throw new FaultToleranceException((Tr.formatMessage(tc, "security.exception.acquiring.fallback.method.CWMFT5004E")), e);
            }
        } else {
            //shouldn't ever reach here since validation should have caught it earlier
            throw new FaultToleranceException(Tr.formatMessage(tc, "internal.error.CWMFT5998E"));
        }
        return fallbackPolicy;
    }

    /**
     * @param <R>
     * @param <R>
     * @param beanInstance
     * @param fallbackMethod
     * @param params
     * @param fallbackReturn
     * @return
     */
    private static <R> FallbackPolicy newFallbackPolicy(Object beanInstance, Method fallbackMethod, Class<R> fallbackReturn) {
        FaultToleranceFunction<ExecutionContext, R> fallbackFunction = new FaultToleranceFunction<ExecutionContext, R>() {
            @Override
            public R execute(ExecutionContext context) throws Exception {
                @SuppressWarnings("unchecked")
                R result = (R) fallbackMethod.invoke(beanInstance, context.getParameters());
                return result;
            }
        };

        FallbackPolicy fallbackPolicy = newFallbackPolicy(fallbackFunction);
        return fallbackPolicy;
    }

    /**
     * @param context
     * @return
     */
    static AggregatedFTPolicy processPolicies(InvocationContext context, BeanManager beanManager) {
        Asynchronous asynchronous = null;
        Retry retry = null;
        CircuitBreaker circuitBreaker = null;
        Timeout timeout = null;
        Bulkhead bulkhead = null;
        Fallback fallback = null;

        //first check the annotations on the target class
        Class<?> targetClass = context.getTarget().getClass();
        Annotation[] annotations = targetClass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Asynchronous.class)) {
                asynchronous = (Asynchronous) annotation;
                asynchronous = new AsynchronousConfig(targetClass, asynchronous);
            } else if (annotation.annotationType().equals(Retry.class)) {
                retry = (Retry) annotation;
                retry = new RetryConfig(targetClass, retry);
            } else if (annotation.annotationType().equals(CircuitBreaker.class)) {
                circuitBreaker = (CircuitBreaker) annotation;
                circuitBreaker = new CircuitBreakerConfig(targetClass, circuitBreaker);
            } else if (annotation.annotationType().equals(Timeout.class)) {
                timeout = (Timeout) annotation;
                timeout = new TimeoutConfig(targetClass, timeout);
            } else if (annotation.annotationType().equals(Bulkhead.class)) {
                bulkhead = (Bulkhead) annotation;
                bulkhead = new BulkheadConfig(targetClass, bulkhead);
            } else if (annotation.annotationType().equals(Fallback.class)) {
                fallback = (Fallback) annotation;
                fallback = new FallbackConfig(targetClass, fallback);
            }
        }

        //then look for annotations on the specific method
        //method level annotations override class level ones
        Method method = context.getMethod();
        annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Asynchronous.class)) {
                asynchronous = (Asynchronous) annotation;
                asynchronous = new AsynchronousConfig(method, asynchronous);
            } else if (annotation.annotationType().equals(Retry.class)) {
                retry = (Retry) annotation;
                retry = new RetryConfig(method, retry);
            } else if (annotation.annotationType().equals(CircuitBreaker.class)) {
                circuitBreaker = (CircuitBreaker) annotation;
                circuitBreaker = new CircuitBreakerConfig(method, circuitBreaker);
            } else if (annotation.annotationType().equals(Timeout.class)) {
                timeout = (Timeout) annotation;
                timeout = new TimeoutConfig(method, timeout);
            } else if (annotation.annotationType().equals(Bulkhead.class)) {
                bulkhead = (Bulkhead) annotation;
                bulkhead = new BulkheadConfig(method, bulkhead);
            } else if (annotation.annotationType().equals(Fallback.class)) {
                fallback = (Fallback) annotation;
                fallback = new FallbackConfig(method, fallback);
            }
        }

        AggregatedFTPolicy policy = new AggregatedFTPolicy();
        if (asynchronous != null) {
            policy.setAsynchronous(true);
        }

        //parse the Timeout annotation
        Duration timeoutDuration = null;
        if (timeout != null) {
            timeoutDuration = Duration.of(timeout.value(), timeout.unit());
            TimeoutPolicy timeoutPolicy = FaultToleranceProvider.newTimeoutPolicy();
            timeoutPolicy.setTimeout(timeoutDuration);
            policy.setTimeoutPolicy(timeoutPolicy);
        }

        //parse the Retry annotation to create a RetryPolicy
        if (retry != null) {
            RetryPolicy retryPolicy = FTAnnotationUtils.processRetryAnnotation(retry);
            policy.setRetryPolicy(retryPolicy);
        }

        //parse the CircuitBreaker annotation to create a CircuitBreakerPolicy
        if (circuitBreaker != null) {
            CircuitBreakerPolicy circuitBreakerPolicy = FTAnnotationUtils.processCircuitBreakerAnnotation(circuitBreaker);
            policy.setCircuitBreakerPolicy(circuitBreakerPolicy);
        }

        if (bulkhead != null) {
            BulkheadPolicy bulkheadPolicy = FTAnnotationUtils.processBulkheadAnnotation(bulkhead);
            policy.setBulkheadPolicy(bulkheadPolicy);
        }

        if (fallback != null) {
            FallbackPolicy fallbackPolicy = processFallbackAnnotation(fallback, context, beanManager);
            policy.setFallbackPolicy(fallbackPolicy);
        }

        return policy;
    }

    private static FallbackHandlerFactory getFallbackHandlerFactory(BeanManager beanManager) {
        FallbackHandlerFactory factory = new FallbackHandlerFactory() {
            @Override
            public <F extends FallbackHandler<?>> F newHandler(Class<F> fallbackClass) {
                AnnotatedType<F> aType = beanManager.createAnnotatedType(fallbackClass);
                CreationalContext<F> cc = beanManager.createCreationalContext(null);
                InjectionTargetFactory<F> factory = beanManager.getInjectionTargetFactory(aType);
                InjectionTarget<F> injectionTarget = factory.createInjectionTarget(null);
                F instance = injectionTarget.produce(cc);
                injectionTarget.inject(instance, cc);
                injectionTarget.postConstruct(instance);
                return instance;
            }
        };
        return factory;
    }

    private static FallbackPolicy newFallbackPolicy(Class<? extends FallbackHandler<?>> fallbackHandlerClass, FallbackHandlerFactory factory) {
        FallbackPolicy fallbackPolicy = FaultToleranceProvider.newFallbackPolicy();
        fallbackPolicy.setFallbackHandler(fallbackHandlerClass, factory);
        return fallbackPolicy;
    }

    private static FallbackPolicy newFallbackPolicy(FaultToleranceFunction<ExecutionContext, ?> fallbackFunction) {
        FallbackPolicy fallbackPolicy = FaultToleranceProvider.newFallbackPolicy();
        fallbackPolicy.setFallbackFunction(fallbackFunction);
        return fallbackPolicy;
    }

    /**
     * @param policies
     * @return
     */
    public static ExecutionBuilder<ExecutionContext, ?> newBuilder(AggregatedFTPolicy policies) {
        ExecutionBuilder<ExecutionContext, ?> builder = FaultToleranceProvider.newExecutionBuilder();
        builder = updateBuilder(builder, policies);
        return builder;
    }

    public static <R> ExecutionBuilder<ExecutionContext, R> updateBuilder(ExecutionBuilder<ExecutionContext, R> builder, AggregatedFTPolicy policies) {
        TimeoutPolicy timeoutPolicy = policies.getTimeoutPolicy();
        CircuitBreakerPolicy circuitBreakerPolicy = policies.getCircuitBreakerPolicy();
        RetryPolicy retryPolicy = policies.getRetryPolicy();
        FallbackPolicy fallbackPolicy = policies.getFallbackPolicy();
        BulkheadPolicy bulkheadPolicy = policies.getBulkheadPolicy();

        if (timeoutPolicy != null) {
            builder.setTimeoutPolicy(timeoutPolicy);
        }
        if (circuitBreakerPolicy != null) {
            builder.setCircuitBreakerPolicy(circuitBreakerPolicy);
        }
        if (retryPolicy != null) {
            builder.setRetryPolicy(retryPolicy);
        }
        if (fallbackPolicy != null) {
            builder.setFallbackPolicy(fallbackPolicy);
        }
        if (bulkheadPolicy != null) {
            builder.setBulkheadPolicy(bulkheadPolicy);
        }
        return builder;
    }
}
