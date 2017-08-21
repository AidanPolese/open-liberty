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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.spi.Execution;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;

@FaultTolerance
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class FaultToleranceInterceptor {

    private static final TraceComponent tc = Tr.register(FaultToleranceInterceptor.class);

    @Inject
    BeanManager beanManager;

    private final ConcurrentHashMap<Method, AggregatedFTPolicy> policyCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<AggregatedFTPolicy, Execution<?>> execCache = new ConcurrentHashMap<>();

    @AroundInvoke
    public Object executeFT(InvocationContext context) throws Throwable {

        AggregatedFTPolicy policy = getFTPolicies(context);

        Object result = execute(context, policy);

        return result;
    }

    /**
     * @param context
     * @return
     */
    private AggregatedFTPolicy getFTPolicies(InvocationContext context) {
        AggregatedFTPolicy policy = null;
        Method method = context.getMethod();
        policy = policyCache.get(method);
        if (policy == null) {
            policy = FTAnnotationUtils.processPolicies(context, beanManager);
            AggregatedFTPolicy previous = policyCache.putIfAbsent(method, policy);
            if (previous != null) {
                policy = previous;
            }
        }
        return policy;
    }

    @FFDCIgnore({ org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException.class })
    private Object execute(InvocationContext invocationContext, AggregatedFTPolicy policies) throws Throwable {

        Execution<?> executor = execCache.get(policies);
        if (executor == null) {

            ExecutionBuilder<ExecutionContext, ?> builder = FTAnnotationUtils.newBuilder(policies);

            if (policies.isAsynchronous()) {
                executor = builder.buildAsync();
            } else {
                executor = builder.build();
            }

            Execution<?> previous = execCache.putIfAbsent(policies, executor);
            if (previous != null) {
                executor = previous;
            }
        }

        ExecutionContextImpl executionContext = new ExecutionContextImpl(invocationContext);

        //if there is a FaultTolerance Executor then run it, otherwise just call proceed
        Object result = null;
        if (executor != null) {
            if (policies.isAsynchronous()) {

                Callable<Future<Object>> callable = () -> {
                    return (Future<Object>) invocationContext.proceed();
                };

                Execution<Future<Object>> async = (Execution<Future<Object>>) executor;
                result = async.execute(callable, executionContext);
            } else {

                Callable<Object> callable = () -> {
                    return invocationContext.proceed();
                };

                Execution<Object> sync = (Execution<Object>) executor;
                try {
                    result = sync.execute(callable, executionContext);
                } catch (org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException e) {
                    throw e.getCause();
                }
            }

        } else {
            result = invocationContext.proceed();
        }
        return result;
    }
}
