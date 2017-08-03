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
package com.ibm.ws.microprofile.faulttolerance.impl.policy;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;

public class FallbackPolicyImpl<R> implements FallbackPolicy<R> {

    private FaultToleranceFunction<ExecutionContext, R> fallback;

    /** {@inheritDoc} */
    @Override
    public FaultToleranceFunction<ExecutionContext, R> getFallback() {
        return fallback;
    }

    /** {@inheritDoc} */
    @Override
    public void setFallback(FaultToleranceFunction<ExecutionContext, R> fallback) {
        this.fallback = fallback;
    }

}
