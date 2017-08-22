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

    public TimeoutConfig(Class<?> annotatedClass, Timeout annotation) {
        super(annotatedClass, annotation, Timeout.class);
    }

    public TimeoutConfig(Method annotatedMethod, Timeout annotation) {
        super(annotatedMethod, annotation, Timeout.class);
    }

    /** {@inheritDoc} */
    @Override
    public long value() {
        return super.getValue("value", long.class);
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit unit() {
        return super.getValue("unit", ChronoUnit.class);
    }
}
