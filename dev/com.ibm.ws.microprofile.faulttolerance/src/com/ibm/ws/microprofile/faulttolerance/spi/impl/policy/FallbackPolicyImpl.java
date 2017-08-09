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
package com.ibm.ws.microprofile.faulttolerance.spi.impl.policy;

import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;

public class FallbackPolicyImpl<T, R> implements FallbackPolicy<T, R> {

    private FaultToleranceFunction<T, R> fallback;

    /** {@inheritDoc} */
    @Override
    public FaultToleranceFunction<T, R> getFallback() {
        return fallback;
    }

    /** {@inheritDoc} */
    @Override
    public void setFallback(FaultToleranceFunction<T, R> fallback) {
        this.fallback = fallback;
    }

}
