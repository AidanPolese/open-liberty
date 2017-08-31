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
package com.ibm.ws.microprofile.faulttolerance.impl;

import java.util.concurrent.Callable;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

@FunctionalInterface
public interface TaskRunner<R> {
    public R runTask(Callable<R> task, ExecutionContext executionContext, TaskContext taskContext) throws Exception;
}
