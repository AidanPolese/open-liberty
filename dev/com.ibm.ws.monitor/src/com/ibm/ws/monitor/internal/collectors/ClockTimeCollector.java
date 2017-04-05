/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.monitor.internal.collectors;

public class ClockTimeCollector {

    long previous;

    long current;

    public long getPrevious() {
        return previous;
    }

    public long getCurrent() {
        return current;
    }

    public long getElapsed() {
        return current - previous;
    }

    public void begin() {
        previous = current;
        current = System.nanoTime();
    }

    public void end() {
        previous = current;
        current = System.nanoTime();
    }
}
