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

import org.eclipse.microprofile.faulttolerance.Timeout;

public class TimeoutConfig extends AbstractAnnotationConfig<Timeout> implements Timeout {

    private final AnnotationParameterConfig<Long> valueConfig = getParameterConfig("value", Long.class);
    private final AnnotationParameterConfig<ChronoUnit> unitConfig = getParameterConfig("unit", ChronoUnit.class);

    public TimeoutConfig(Class<?> annotatedClass, Timeout annotation) {
        super(annotatedClass, annotation, Timeout.class);
    }

    public TimeoutConfig(Method annotatedMethod, Class<?> annotatedClass, Timeout annotation) {
        super(annotatedMethod, annotatedClass, annotation, Timeout.class);
    }

    /** {@inheritDoc} */
    @Override
    public long value() {
        return valueConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit unit() {
        return unitConfig.getValue();
    }
}
