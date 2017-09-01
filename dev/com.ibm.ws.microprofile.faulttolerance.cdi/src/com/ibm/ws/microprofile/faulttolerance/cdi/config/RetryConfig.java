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

import org.eclipse.microprofile.faulttolerance.Retry;

public class RetryConfig extends AbstractAnnotationConfig<Retry> implements Retry {

    private final AnnotationParameterConfig<Integer> maxRetriesConfig = getParameterConfig("maxRetries", Integer.class);
    private final AnnotationParameterConfig<Long> delayConfig = getParameterConfig("delay", Long.class);
    private final AnnotationParameterConfig<ChronoUnit> delayUnitConfig = getParameterConfig("delayUnit", ChronoUnit.class);
    private final AnnotationParameterConfig<Long> maxDurationConfig = getParameterConfig("maxDuration", Long.class);
    private final AnnotationParameterConfig<ChronoUnit> durationUnitConfig = getParameterConfig("durationUnit", ChronoUnit.class);
    private final AnnotationParameterConfig<Long> jitterConfig = getParameterConfig("jitter", Long.class);
    private final AnnotationParameterConfig<ChronoUnit> jitterDelayUnitConfig = getParameterConfig("jitterDelayUnit", ChronoUnit.class);
    private final AnnotationParameterConfig<Class<? extends Throwable>[]> retryOnConfig = getParameterConfigClassArray("retryOn", Throwable.class);
    private final AnnotationParameterConfig<Class<? extends Throwable>[]> abortOnConfig = getParameterConfigClassArray("abortOn", Throwable.class);

    public RetryConfig(Class<?> annotatedClass, Retry annotation) {
        super(annotatedClass, annotation, Retry.class);
    }

    public RetryConfig(Method annotatedMethod, Class<?> annotatedClass, Retry annotation) {
        super(annotatedMethod, annotatedClass, annotation, Retry.class);
    }

    /** {@inheritDoc} */
    @Override
    public int maxRetries() {
        return maxRetriesConfig.getValue();
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
    public long maxDuration() {
        return maxDurationConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit durationUnit() {
        return durationUnitConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public long jitter() {
        return jitterConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit jitterDelayUnit() {
        return jitterDelayUnitConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Throwable>[] retryOn() {
        return retryOnConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Throwable>[] abortOn() {
        return abortOnConfig.getValue();
    }
}
