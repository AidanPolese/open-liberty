/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.diagnostics.java;

import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import com.ibm.wsspi.logging.Introspector;

/**
 * Gather information about all threads from the {@link ThreadMXBean}.
 */
public class ThreadInfoIntrospector implements Introspector {
    private final static String INDENT = "    ";

    @Override
    public String getIntrospectorName() {
        return "ThreadInfoIntrospector";
    }

    @Override
    public String getIntrospectorDescription() {
        return "Basic thread information acquired from the SDK Thread MXBean";
    }

    @Override
    public void introspect(PrintWriter writer) {
        // Grab the Thread MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // Dump count information
        writer.println("Thread counts:");
        writer.println("--------------");
        writer.println(INDENT + "current active: " + threadMXBean.getThreadCount());
        writer.println(INDENT + " active daemon: " + threadMXBean.getDaemonThreadCount());
        writer.println(INDENT + "   peak active: " + threadMXBean.getPeakThreadCount());
        writer.println(INDENT + " total started: " + threadMXBean.getTotalStartedThreadCount());
        writer.println();

        // Get the lock monitor support flags
        boolean lockedMonitorsSupported = threadMXBean.isObjectMonitorUsageSupported();
        boolean lockedSynchronizersSupported = threadMXBean.isSynchronizerUsageSupported();

        if (lockedMonitorsSupported && lockedSynchronizersSupported) {
            introspectDeadlockedThreads(threadMXBean, writer);
        } else if (lockedMonitorsSupported) {
            introspectMonitorDeadlockedThreads(threadMXBean, writer);
        }

        writer.println();
        writer.println("All thread information:");
        writer.println("-----------------------");
        dumpThreadInfos(threadMXBean.dumpAllThreads(lockedMonitorsSupported, lockedSynchronizersSupported), writer);
    }

    void introspectDeadlockedThreads(ThreadMXBean threadMXBean, PrintWriter writer) {
        writer.println("Detected deadlocks:");
        writer.println("-------------------");

        long[] deadlocked = threadMXBean.findDeadlockedThreads();
        if (deadlocked == null) {
            writer.println("No deadlocks detected");
            return;
        }

        // Get the thread information from the ids
        dumpThreadInfos(threadMXBean.getThreadInfo(deadlocked), writer);
    }

    void introspectMonitorDeadlockedThreads(ThreadMXBean threadMXBean, PrintWriter writer) {
        writer.println("Detected deadlocks:");
        writer.println("-------------------");

        long[] deadlocked = threadMXBean.findMonitorDeadlockedThreads();
        if (deadlocked == null) {
            writer.println("No deadlocks detected");
            return;
        }

        // Get the thread information from the ids
        dumpThreadInfos(threadMXBean.getThreadInfo(deadlocked), writer);
    }

    void dumpThreadInfos(ThreadInfo[] threadInfos, PrintWriter writer) {
        for (ThreadInfo threadInfo : threadInfos) {
            writer.println(threadInfo);
        }
    }
}
