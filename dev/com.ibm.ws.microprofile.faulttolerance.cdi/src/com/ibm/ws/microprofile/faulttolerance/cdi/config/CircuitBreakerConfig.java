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
package com.ibm.ws.microprofile.faulttolerance.cdi.config;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

public class CircuitBreakerConfig extends AbstractAnnotationConfig<CircuitBreaker> implements CircuitBreaker {

    private final AnnotationParameterConfig<Class<? extends Throwable>[]> failOnConfig = getParameterConfigClassArray("failOn", Throwable.class);
    private final AnnotationParameterConfig<Long> delayConfig = getParameterConfig("delay", Long.class);
    private final AnnotationParameterConfig<ChronoUnit> delayUnitConfig = getParameterConfig("delayUnit", ChronoUnit.class);
    private final AnnotationParameterConfig<Integer> requestVolumeThresholdConfig = getParameterConfig("requestVolumeThreshold", Integer.class);
    private final AnnotationParameterConfig<Double> failureRatioConfig = getParameterConfig("failureRatio", Double.class);
    private final AnnotationParameterConfig<Integer> successThresholdConfig = getParameterConfig("successThreshold", Integer.class);

    public CircuitBreakerConfig(Class<?> annotatedClass, CircuitBreaker annotation) {
        super(annotatedClass, annotation, CircuitBreaker.class);
    }

    public CircuitBreakerConfig(Method annotatedMethod, Class<?> annotatedClass, CircuitBreaker annotation) {
        super(annotatedMethod, annotatedClass, annotation, CircuitBreaker.class);
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Throwable>[] failOn() {
        return failOnConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public long delay() {
        return delayConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit delayUnit() {
        return delayUnitConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public int requestVolumeThreshold() {
        return requestVolumeThresholdConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public double failureRatio() {
        return failureRatioConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public int successThreshold() {
        return successThresholdConfig.getValue();
    }
}
