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

package com.ibm.ws.monitor.internal.boot.templates;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ProbeUtils {

    private final static ThreadMXBean threadBean = getThreadMXBean();

    public static final long nanoTime() {
        return System.nanoTime();
    }

    public static final long elapsedNanoTime(long startTime) {
        return nanoTime() - startTime;
    }

    public static final long cpuTime() {
        if (threadBean != null) {
            return threadBean.getCurrentThreadCpuTime();
        }
        return -1L;
    }

    public static final long elapsedCpuTime(long startTime) {
        return cpuTime() - startTime;
    }

    public static final long userTime() {
        if (threadBean != null) {
            return threadBean.getCurrentThreadUserTime();
        }
        return -1L;
    }

    public static final long elapsedUserTime(long startTime) {
        return userTime() - startTime;
    }

    private static ThreadMXBean getThreadMXBean() {
        return AccessController.doPrivileged(new PrivilegedAction<ThreadMXBean>() {
            public ThreadMXBean run() {
                ThreadMXBean bean = ManagementFactory.getThreadMXBean();
                try {
                    if (bean != null && !bean.isCurrentThreadCpuTimeSupported()) {
                        bean = null;
                    }
                    if (bean != null && !bean.isThreadCpuTimeEnabled()) {
                        bean.setThreadCpuTimeEnabled(true);
                    }
                } catch (Throwable t) {
                    bean = null;
                }
                return bean;
            }
        });
    }
}
