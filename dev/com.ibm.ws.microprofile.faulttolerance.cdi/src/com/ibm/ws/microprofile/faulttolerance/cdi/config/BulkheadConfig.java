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

import org.eclipse.microprofile.faulttolerance.Bulkhead;

public class BulkheadConfig extends AbstractAnnotationConfig<Bulkhead> implements Bulkhead {

    private final AnnotationParameterConfig<Integer> valueConfig = getParameterConfig("value", Integer.class);
    private final AnnotationParameterConfig<Integer> waitingTaskQueueConfig = getParameterConfig("waitingTaskQueue", Integer.class);

    public BulkheadConfig(Class<?> annotatedClass, Bulkhead annotation) {
        super(annotatedClass, annotation, Bulkhead.class);
    }

    public BulkheadConfig(Method annotatedMethod, Class<?> annotatedClass, Bulkhead annotation) {
        super(annotatedMethod, annotatedClass, annotation, Bulkhead.class);
    }

    /** {@inheritDoc} */
    @Override
    public int value() {
        return valueConfig.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public int waitingTaskQueue() {
        return waitingTaskQueueConfig.getValue();
    }
}
