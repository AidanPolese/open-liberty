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
package com.ibm.ws.microprofile.faulttolerance.test.util;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;

/**
 *
 */
public class TestFallback implements FaultToleranceFunction<ExecutionContext, String> {

    /** {@inheritDoc} */
    @Override
    public String execute(ExecutionContext context) throws Exception {
        return "Fallback: " + context.getParameters()[0];
    }

}
