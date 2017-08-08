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
package web;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Task that submits another task.
 * This is useful when we are expecting the task submission to be blocked for a period of time,
 * such as when the queue is full and a maxWaitForEnqueue is configured or the queue full action is
 * one of the caller runs options.
 * @param <T>
 */
public class SubmitterTask<T> implements Callable<Future<T>> {
    private final ExecutorService executor;
    private final Callable<?> callable;

    public SubmitterTask(ExecutorService executor, Callable<T> callable) {
        this.executor = executor;
        this.callable = callable;
    }

    @Override
    public Future<T> call() {
        System.out.println("> call " + toString());
        try {
            @SuppressWarnings("unchecked")
            Future<T> future = (Future<T>) executor.submit(callable);
            System.out.println("< call " + toString() + " " + future);
            return future;
        } catch (RuntimeException x) {
            System.out.println("< call " + toString() + " " + x);
            throw x;
        }
    }
}
