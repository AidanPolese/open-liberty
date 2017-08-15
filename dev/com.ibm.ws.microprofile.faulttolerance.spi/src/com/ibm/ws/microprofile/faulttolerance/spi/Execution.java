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

import java.util.concurrent.Callable;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

/**
 *
 */
public interface Execution<R> {

    public R execute(Callable<R> callable, ExecutionContext context);

}
