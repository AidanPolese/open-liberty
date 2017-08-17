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

    public CircuitBreakerConfig(Class<?> annotatedClass, CircuitBreaker annotation) {
        super(annotatedClass, annotation);
    }

    public CircuitBreakerConfig(Method annotatedMethod, CircuitBreaker annotation) {
        super(annotatedMethod, annotation);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Throwable>[] failOn() {
        return super.getValue("failOn", Class[].class);
    }

    /** {@inheritDoc} */
    @Override
    public long delay() {
        return super.getValue("delay", long.class);
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit delayUnit() {
        return super.getValue("delayUnit", ChronoUnit.class);
    }

    /** {@inheritDoc} */
    @Override
    public int requestVolumeThreshold() {
        return super.getValue("requestVolumeThreshold", int.class);
    }

    /** {@inheritDoc} */
    @Override
    public double failureRatio() {
        return super.getValue("failureRatio", double.class);
    }

    /** {@inheritDoc} */
    @Override
    public int successThreshold() {
        return super.getValue("successThreshold", int.class);
    }
}
