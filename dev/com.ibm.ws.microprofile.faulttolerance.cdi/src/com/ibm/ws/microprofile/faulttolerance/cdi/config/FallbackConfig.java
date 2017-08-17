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

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

public class FallbackConfig extends AbstractAnnotationConfig<Fallback> implements Fallback {

    public FallbackConfig(Class<?> annotatedClass, Fallback annotation) {
        super(annotatedClass, annotation);
    }

    public FallbackConfig(Method annotatedMethod, Fallback annotation) {
        super(annotatedMethod, annotation);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends FallbackHandler<?>> value() {
        return super.getValue("value", Class.class);
    }

    /** {@inheritDoc} */
    @Override
    public String fallbackMethod() {
        return super.getValue("fallbackMethod", String.class);
    }
}
