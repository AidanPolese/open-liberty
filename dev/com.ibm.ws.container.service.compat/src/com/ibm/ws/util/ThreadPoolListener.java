/*
 * IBM Confidential OCO Source Material
 * 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * 1.3, 5/26/02
 */
package com.ibm.ws.util;

public interface ThreadPoolListener {
    public void threadPoolCreated(ThreadPool tp);

    public void threadCreated(ThreadPool tp, int poolSize);

    public void threadStarted(ThreadPool tp, int activeThreads, int maxThreads);

    public void threadReturned(ThreadPool tp, int activeThreads, int maxThreads);

    public void threadDestroyed(ThreadPool tp, int poolSize);
}
