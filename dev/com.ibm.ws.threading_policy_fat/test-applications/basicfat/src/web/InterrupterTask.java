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

import java.util.concurrent.TimeUnit;

/**
 * Task that interrupts another thread after the specified amount of time.
 */
public class InterrupterTask implements Runnable {
    private final Thread thread;
    private final long timeout;
    private final TimeUnit unit;

    public InterrupterTask(Thread thread, long timeout, TimeUnit unit) {
        this.thread = thread;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void run() {
        System.out.println("> run " + toString());
        try {
            unit.sleep(timeout);
            thread.interrupt();
            System.out.println("< run " + toString());
        } catch (InterruptedException x) {
            System.out.println("< run " + toString() + " " + x);
            throw new RuntimeException(x);
        } catch (RuntimeException x) {
            System.out.println("< run " + toString() + " " + x);
            throw x;
        }
    }
}
