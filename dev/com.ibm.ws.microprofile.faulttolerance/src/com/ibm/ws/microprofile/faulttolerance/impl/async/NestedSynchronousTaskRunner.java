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

import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;

/**
 *
 */
public class NestedSynchronousTaskRunner<R> implements TaskRunner<R> {

    @Override
    public R runTask(Callable<R> task, ExecutionContextImpl executionContext) throws Exception {
        R result = null;
        try {
            result = task.call();
        } finally {
            executionContext.end();
        }

        return result;
    }

}
