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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class CpuTimeCollector {

    private final static ThreadMXBean threadBean = getThreadMXBean();
    private static final TraceComponent tc = Tr.register(CpuTimeCollector.class);

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
        if (threadBean == null)
            return;

        previous = current;
        current = threadBean.getCurrentThreadCpuTime();
    }

    public void end() {
        if (threadBean == null)
            return;

        previous = current;
        current = threadBean.getCurrentThreadCpuTime();
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
