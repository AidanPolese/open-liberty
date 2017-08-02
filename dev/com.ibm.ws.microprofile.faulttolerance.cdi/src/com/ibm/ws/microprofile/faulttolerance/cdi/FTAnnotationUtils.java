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
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
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

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

public class FTAnnotationUtils {

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

    static BulkheadPolicy processBulkheadAnnotation(Bulkhead bulkhead, Duration timeoutDuration) {
        int maxThreads = bulkhead.value();
        int maxQueue = bulkhead.waitingTaskQueue();

        BulkheadPolicy bulkheadPolicy = FaultToleranceProvider.newBulkheadPolicy();

        bulkheadPolicy.setMaxThreads(maxThreads);
        bulkheadPolicy.setMaxQueue(maxQueue);
        if (timeoutDuration != null) {
            bulkheadPolicy.setTimeout(timeoutDuration);
        }

        return bulkheadPolicy;
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
            } else if (annotation.annotationType().equals(Retry.class)) {
                retry = (Retry) annotation;
            } else if (annotation.annotationType().equals(CircuitBreaker.class)) {
                circuitBreaker = (CircuitBreaker) annotation;
            } else if (annotation.annotationType().equals(Timeout.class)) {
                timeout = (Timeout) annotation;
            } else if (annotation.annotationType().equals(Bulkhead.class)) {
                bulkhead = (Bulkhead) annotation;
            } else if (annotation.annotationType().equals(Fallback.class)) {
                fallback = (Fallback) annotation;
            }
        }

        //then look for annotations on the specific method
        //method level annotations override class level ones
        Method method = context.getMethod();
        annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Asynchronous.class)) {
                asynchronous = (Asynchronous) annotation;
            } else if (annotation.annotationType().equals(Retry.class)) {
                retry = (Retry) annotation;
            } else if (annotation.annotationType().equals(CircuitBreaker.class)) {
                circuitBreaker = (CircuitBreaker) annotation;
            } else if (annotation.annotationType().equals(Timeout.class)) {
                timeout = (Timeout) annotation;
            } else if (annotation.annotationType().equals(Bulkhead.class)) {
                bulkhead = (Bulkhead) annotation;
            } else if (annotation.annotationType().equals(Fallback.class)) {
                fallback = (Fallback) annotation;
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
            BulkheadPolicy bulkheadPolicy = FTAnnotationUtils.processBulkheadAnnotation(bulkhead, timeoutDuration);
            policy.setBulkheadPolicy(bulkheadPolicy);
        }

        if (fallback != null) {
            Class<? extends FallbackHandler<?>> fallbackClass = fallback.value();
            FallbackHandler<?> fallbackHandler = newNonContextual(fallbackClass, beanManager);
            FallbackPolicy<ExecutionContext, ?> fallbackPolicy = newFallbackPolicy(fallbackHandler);
            policy.setFallbackPolicy(fallbackPolicy);
        }

        return policy;
    }

    /**
     * @param fallbackClass
     * @param beanManager
     * @return
     */
    private static <F> F newNonContextual(Class<F> fallbackClass, BeanManager beanManager) {
        AnnotatedType<F> aType = beanManager.createAnnotatedType(fallbackClass);
        CreationalContext<F> cc = beanManager.createCreationalContext(null);
        InjectionTargetFactory<F> factory = beanManager.getInjectionTargetFactory(aType);
        InjectionTarget<F> injectionTarget = factory.createInjectionTarget(null);
        F instance = injectionTarget.produce(cc);
        injectionTarget.inject(instance, cc);
        injectionTarget.postConstruct(instance);
        return instance;
    }

    private static <R> FallbackPolicy<ExecutionContext, R> newFallbackPolicy(FallbackHandler<R> fallbackHandler) {
        FallbackFunction<R> fallbackCallable = new FallbackFunction<>(fallbackHandler);
        FallbackPolicy<ExecutionContext, R> fallbackPolicy = FaultToleranceProvider.newFallbackPolicy();
        fallbackPolicy.setFallback(fallbackCallable);
        return fallbackPolicy;
    }

    public static Method getMethod(Class<?> targetClass, String methodName, Class<?>... params) {
        Method method = AccessController.doPrivileged(new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    return targetClass.getMethod(methodName, params);
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new FaultToleranceException(e);
                }
            }
        });
        return method;
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
        FallbackPolicy<ExecutionContext, R> fallbackPolicy = (FallbackPolicy<ExecutionContext, R>) policies.getFallbackPolicy();
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
