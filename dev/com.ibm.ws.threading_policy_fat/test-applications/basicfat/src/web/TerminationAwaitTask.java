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
import java.util.concurrent.TimeUnit;

/**
 * Task that awaits termination of an executor.
 */
public class TerminationAwaitTask implements Callable<Boolean> {
    private final ExecutorService executor;
    private final long timeout;

    public TerminationAwaitTask(ExecutorService executor, long nanos) {
        this.executor = executor;
        this.timeout = nanos;
    }

    @Override
    public Boolean call() throws InterruptedException {
        System.out.println("> call " + toString());
        try {
            boolean terminated = executor.awaitTermination(timeout, TimeUnit.NANOSECONDS);
            System.out.println("< call " + toString() + " " + terminated);
            return terminated;
        } catch (InterruptedException x) {
            System.out.println("< call " + toString() + " " + x);
            throw x;
        }
    }
}
