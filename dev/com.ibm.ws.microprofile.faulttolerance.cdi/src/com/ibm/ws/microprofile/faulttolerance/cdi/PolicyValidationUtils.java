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

import java.lang.reflect.Method;
import java.util.concurrent.Future;

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
import com.ibm.ws.microprofile.faulttolerance.cdi.config.BulkheadConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.CircuitBreakerConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.FallbackConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.RetryConfig;
import com.ibm.ws.microprofile.faulttolerance.cdi.config.TimeoutConfig;

/**
 * A utility class to validate the FT policies
 */
public class PolicyValidationUtils {
    private static final TraceComponent tc = Tr.register(PolicyValidationUtils.class);

    /**
     * Validate Asynchronous annotation to make sure all methods with this annotation specified returns a Future.
     * If placed on class-level, all declared methods in this class will need to return a Future.
     *
     * @param method the method to be validated
     *
     */
    public static void validateAsynchronous(Method method) {
        Class<?> originalMethodReturnType = method.getReturnType();
        if (!(Future.class.isAssignableFrom(originalMethodReturnType))) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "asynchronous.method.not.returning.future.CWMFT5001E", method));
        }
    }

    /**
     * Validate the Retry policy to make sure all the parameters e.g. maxRetries, delay, jitter, maxDuration must not be negative.
     *
     * @param clazz the class containing Retry annotation
     * @param method the method that the Retry annotation specified
     * @param retry the Retry annotation
     */
    public static void validateRetry(Class<?> clazz, Method method, Retry retry) {

        RetryConfig retryConfig = null;
        String target = null;
        if (method == null) {
            retryConfig = new RetryConfig(clazz, retry);
            target = clazz.getName();
        } else {
            retryConfig = new RetryConfig(method, retry);
            target = clazz.getName() + "." + method.getName();
        }

        //validate the parameters
        if (retryConfig.maxRetries() < -1) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "retry.parameter.invalid.value.CWMFT5010E", "maxRetries", retryConfig.maxRetries(), target, "-1"));
        }
        if ((retryConfig.delay() < 0)) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "retry.parameter.invalid.value.CWMFT5010E", "delay", retryConfig.delay(), target, "0"));
        }
        if ((retryConfig.jitter() < 0)) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "retry.parameter.invalid.value.CWMFT5010E", "jitter", retryConfig.jitter(), target, "0"));
        }
        if (retryConfig.maxDuration() < 0) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "retry.parameter.invalid.value.CWMFT5010E", "maxDuration", retryConfig.maxDuration(), target, "0"));
        }
        if ((retryConfig.maxDuration() != 0) && (retryConfig.maxDuration() < retryConfig.delay())) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "retry.parameter.invalid.value.CWMFT5017E", "maxDuration", retryConfig.maxDuration(), target, "delay",
                                                               retryConfig.delay()));

        }
    }

    /**
     * Validate the Timeout policy to make sure all the parameters e.g. maxRetries, delay, jitter, maxDuration must not be negative.
     *
     * @param clazz the class containing Timeout annotation
     * @param method the method that the Timeout annotation specified
     * @param timeout the Timeout annotation
     */
    public static void validateTimeout(Class<?> clazz, Method method, Timeout retry) {

        TimeoutConfig timeoutConfig = null;
        String target = null;
        if (method == null) {
            timeoutConfig = new TimeoutConfig(clazz, retry);
            target = clazz.getName();
        } else {
            timeoutConfig = new TimeoutConfig(method, retry);
            target = clazz.getName() + "." + method.getName();
        }

        //validate the parameters
        if (timeoutConfig.value() < 0) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "timeout.parameter.invalid.value.CWMFT5011E", timeoutConfig.value(), target));
        }

    }

    /**
     * Validate the CircuitBreaker policy
     *
     * @param clazz the class containing CircuitBreaker annotation
     * @param method the method that the CircuitBreaker annotation specified
     * @param cb the CircuitBreaker annotation
     */
    public static void validateCircuitBreaker(Class<?> clazz, Method method, CircuitBreaker cb) {

        CircuitBreakerConfig cbConfig = null;
        String target = null;
        if (method == null) {
            cbConfig = new CircuitBreakerConfig(clazz, cb);
            target = clazz.getName();
        } else {
            cbConfig = new CircuitBreakerConfig(method, cb);
            target = clazz.getName() + "." + method.getName();
        }

        //validate the parameters
        if (cbConfig.delay() < 0) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "circuitBreaker.parameter.delay.invalid.value.CWMFT5012E", "delay", cbConfig.delay(), target));
        }
        if ((cbConfig.failureRatio() < 0) || (cbConfig.failureRatio() > 1)) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "circuitBreaker.parameter.failureRatio.invalid.value.CWMFT5013E", "failureRatio", cbConfig.failureRatio(),
                                                               target));
        }
        if (cbConfig.requestVolumeThreshold() < 1) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "circuitBreaker.parameter.requestVolumeThreshold.invalid.value.CWMFT5014E", "requestVolumeThreshold",
                                                               cbConfig.requestVolumeThreshold(), target));
        }
        if (cbConfig.successThreshold() < 1) {

            throw new FaultToleranceException(Tr.formatMessage(tc, "circuitBreaker.parameter.successThreshold.invalid.value.CWMFT5015E", "successThreshold",
                                                               cbConfig.successThreshold(), target));
        }

    }

    /**
     * Validate Bulkhead configure and make sure the value and waitingTaskQueue must be greater than or equal to 1.
     *
     * @param clazz the class containing Bulkhead annotation
     * @param method the method that the Bulkhead annotation specified
     * @param bulkhead the Bulkhead annotation
     */
    public static void validateBulkhead(Class<?> clazz, Method method, Bulkhead bulkhead) {

        BulkheadConfig bulkheadConfig = null;
        String target = null;
        if (method == null) {
            bulkheadConfig = new BulkheadConfig(clazz, bulkhead);
            target = clazz.getName();
        } else {
            bulkheadConfig = new BulkheadConfig(method, bulkhead);
            target = clazz.getName() + "." + method.getName();
        }

        //validate the parameters
        if (bulkheadConfig.value() < 1) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "timeout.parameter.invalid.value.CWMFT5011E", "value ", bulkheadConfig.value(), target));
        }
        //validate the parameters
        if (bulkheadConfig.waitingTaskQueue() < 1) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "timeout.parameter.invalid.value.CWMFT5011E", "waitingTaskQueue", bulkhead.waitingTaskQueue(), target));
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
    public static void validateFallback(Method originalMethod, Fallback annotation) {
        //validate the fallback annotation

        FallbackConfig fb = new FallbackConfig(originalMethod, annotation);

        Class<?> originalMethodReturnType = originalMethod.getReturnType();
        Class<?>[] originalMethodParamTypes = originalMethod.getParameterTypes();

        Class<? extends FallbackHandler<?>> fallbackClass = fb.value();
        String fallbackMethodName = fb.fallbackMethod();
        //If both fallback method and fallback class are set, it is an illegal state.
        if ((fallbackClass != null && fallbackClass != Fallback.DEFAULT.class) && (fallbackMethodName != null && !"".equals(fallbackMethodName))) {
            throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.policy.conflicts.CWMFT5009E", originalMethod, fallbackClass, fallbackMethodName));
        } else if (fallbackClass != null && fallbackClass != Fallback.DEFAULT.class) {
            //need to load the fallback class and then find out the method return type
            try {
                Method[] ms = fallbackClass.getMethods();
                Method handleMethod = FallbackHandler.class.getMethod(FTUtils.FALLBACKHANDLE_METHOD_NAME, ExecutionContext.class);
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

            } catch (NoSuchMethodException e) {
                throw new FaultToleranceException(Tr.formatMessage(tc, "fallback.method.not.found.CWMFT5003E", fallbackMethodName,
                                                                   originalMethod.getName(), originalMethod.getDeclaringClass()), e);
            } catch (SecurityException e) {
                throw new FaultToleranceException((Tr.formatMessage(tc, "security.exception.acquiring.fallback.method.CWMFT5004E")), e);
            }

        }
    }
}
