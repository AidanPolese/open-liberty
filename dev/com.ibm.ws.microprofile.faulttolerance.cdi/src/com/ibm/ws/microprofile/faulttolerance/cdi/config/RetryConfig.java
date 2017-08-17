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

    public RetryConfig(Class<?> annotatedClass, Retry annotation) {
        super(annotatedClass, annotation);
    }

    public RetryConfig(Method annotatedMethod, Retry annotation) {
        super(annotatedMethod, annotation);
    }

    /** {@inheritDoc} */
    @Override
    public int maxRetries() {
        return super.getValue("maxRetries", int.class);
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
    public long maxDuration() {
        return super.getValue("maxDuration", long.class);
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit durationUnit() {
        return super.getValue("durationUnit", ChronoUnit.class);
    }

    /** {@inheritDoc} */
    @Override
    public long jitter() {
        return super.getValue("jitter", long.class);
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit jitterDelayUnit() {
        return super.getValue("jitterDelayUnit", ChronoUnit.class);
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Throwable>[] retryOn() {
        return super.getValue("retryOn", Class[].class);
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Throwable>[] abortOn() {
        return super.getValue("abortOn", Class[].class);
    }
}
