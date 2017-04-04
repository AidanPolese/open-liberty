/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A slight enhancement over simple Object.wait() and notify(), this class allows us to check
 * that something significant actually happened to occasion a wake-up. This helps avoid the
 * spurious wake-up phenomenon for which Java's Object.wait() is so famous.
 */
class EventLock implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AtomicInteger eventCount = new AtomicInteger(0);

    final int getEventCount() {
        return eventCount.get();
    }

    /**
     * Determine whether any new events have been posted.
     * 
     * @param oldEventCount the number of events at the start time, for comparison
     * @return <code>true</code> iff a new event has been posted.
     */
    final boolean eventPosted(int oldEventCount) {
        return eventCount.get() != oldEventCount;
    }

    /** Post an event. */
    void postEvent() {
        // note that a new event has been posted
        eventCount.incrementAndGet();
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Wait for an event to be posted.
     * 
     * @param oldEventCount keep the old eventCount on the stack for later comparison
     * @return <code>true</code> if an event was posted, and
     *         <code>false</code> if the wait timed out.
     * @throws InterruptedException if the thread was interrupted while waiting.
     */
    synchronized boolean wait(int oldEventCount) throws InterruptedException {
        while (!!!eventPosted(oldEventCount)) {
            this.wait();
        }
        return true;
    }

    boolean canTimeOut() {
        return false;
    }

    boolean hasTimedOut() {
        return false;
    }
}