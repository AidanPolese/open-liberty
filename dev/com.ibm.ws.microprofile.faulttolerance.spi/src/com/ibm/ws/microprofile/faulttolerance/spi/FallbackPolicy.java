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
package com.ibm.ws.microprofile.faulttolerance.spi;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

public interface FallbackPolicy<R> {

    public FaultToleranceFunction<ExecutionContext, R> getFallbackFunction();

    public void setFallbackFunction(FaultToleranceFunction<ExecutionContext, R> fallback);

    public Class<? extends FallbackHandler<R>> getFallbackHandler();

    public void setFallbackHandler(Class<? extends FallbackHandler<R>> clazz, FallbackHandlerFactory factory);

    public FallbackHandlerFactory getFallbackHandlerFactory();
}
