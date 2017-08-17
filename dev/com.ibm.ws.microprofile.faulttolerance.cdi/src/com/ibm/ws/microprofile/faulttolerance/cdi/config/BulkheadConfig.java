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

    public BulkheadConfig(Class<?> annotatedClass, Bulkhead annotation) {
        super(annotatedClass, annotation);
    }

    public BulkheadConfig(Method annotatedMethod, Bulkhead annotation) {
        super(annotatedMethod, annotation);
    }

    /** {@inheritDoc} */
    @Override
    public int value() {
        return super.getValue("value", int.class);
    }

    /** {@inheritDoc} */
    @Override
    public int waitingTaskQueue() {
        return super.getValue("waitingTaskQueue", int.class);
    }
}
