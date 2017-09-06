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
package com.ibm.ws.microprofile.faulttolerance.impl.async;

import java.util.concurrent.Callable;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.impl.sync.SynchronousExecutorImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;

/**
 *
 */
public class NestedExecutorImpl<R> extends SynchronousExecutorImpl<R> implements Executor<R> {

    private final TaskRunner<R> taskRunner;

    //internal constructor for the nested synchronous part of an asynchronous execution
    public NestedExecutorImpl() {
        this.taskRunner = new NestedSynchronousTaskRunner<>();
    }

    @Override
    protected TaskRunner<R> getTaskRunner() {
        return this.taskRunner;
    }

    @Override
    public R execute(Callable<R> callable, ExecutionContext executionContext) {
        ExecutionContextImpl executionContextImpl = (ExecutionContextImpl) executionContext;
        executionContextImpl.setNested();
        R result = super.execute(callable, executionContextImpl);
        return result;
    }
}
