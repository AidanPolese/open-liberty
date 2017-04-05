/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.monitors.helper;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 *
 */
public class JvmMonitorHelper {

    private final MemoryMXBean mmx;
    private final List<GarbageCollectorMXBean> gmx;
    private final GarbageCollectorMXBean firstGCMBean;
    private final RuntimeMXBean rmx;
    private final OperatingSystemMXBean osmx;
    private final Method met_getProcessCpuTime;

    private static final String PROCESSCPU_METHOD_NAME = "getProcessCpuTime";

    //For CPU
    private long currElapsedCPUTime = 0;
    private long currElapsedRealTime = 0;
    private long lastElapsedRealTime = 0;
    private long lastElapsedCPUTime = 0;
    private int cpuNSFactor = 1;

    /**
     * 
     */
    public JvmMonitorHelper() {
        mmx = ManagementFactory.getMemoryMXBean();
        gmx = ManagementFactory.getGarbageCollectorMXBeans();
        firstGCMBean = gmx.get(0);
        rmx = ManagementFactory.getRuntimeMXBean();
        if (rmx.getVmVendor().equalsIgnoreCase("IBM Corporation")) {
            //IBM Implementation of getProcessCpuTime calculates CPU time per 100 nano-seconds.
            cpuNSFactor = 100;
        }
        osmx = ManagementFactory.getOperatingSystemMXBean();
        met_getProcessCpuTime = getProcessCpuTimeMethod(osmx.getClass());
    }

    @FFDCIgnore(NoSuchMethodException.class)
    private Method getProcessCpuTimeMethod(Class<?> osmxClass) {
        try {
            return osmxClass.getMethod(PROCESSCPU_METHOD_NAME);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Method : getCommitedHeapMemoryUsage().
     * 
     * @return the memory, which is committed to use for this JVM.
     *         Always query and give latest value.
     */
    public long getCommitedHeapMemoryUsage() {
        MemoryUsage mu = mmx.getHeapMemoryUsage();
        return mu.getCommitted();
    }

    /**
     * Method : getInitHeapMemorySettings().
     * 
     * @return the memory, which initially asked by this JVM.
     */
    public long getInitHeapMemorySettings() {
        MemoryUsage mu = mmx.getHeapMemoryUsage();
        return mu.getInit();
    }

    /**
     * Method : getMaxHeapMemorySettings().
     * 
     * @return max memory, that can be used by this JVM.
     */
    public long getMaxHeapMemorySettings() {
        MemoryUsage mu = mmx.getHeapMemoryUsage();
        return mu.getMax();
    }

    /**
     * Method : getUsedHeapMemoryUsage.
     * 
     * @return amount of memory used in bytes.
     */
    public long getUsedHeapMemoryUsage() {
        MemoryUsage mu = mmx.getHeapMemoryUsage();
        return mu.getUsed();
    }

    /**
     * Method getGCCollectionCount
     * 
     * @return The total number of collections that have occurred.
     */
    public long getGCCollectionCount() {
        return firstGCMBean.getCollectionCount();
    }

    /**
     * Method : getGCCollectionTime
     * 
     * @return The approximate accumulated collection elapsed time in milliseconds.
     */
    public long getGCCollectionTime() {
        return firstGCMBean.getCollectionTime();
    }

    /**
     * Method : getUptime()
     * 
     * @return Returns the uptime of the Java virtual machine in milliseconds.
     */
    public long getUptime() {
        return rmx.getUptime();

    }

    /**
     * 
     * Method : getCPU
     * 
     * @return Percentage CPU usage for JVM Process
     */
    public double getCPU() {
        double cpuUsage = 0;
        if (met_getProcessCpuTime != null && (!met_getProcessCpuTime.isAccessible())) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    met_getProcessCpuTime.setAccessible(true);
                    return null;
                }
            });
        }

        if (met_getProcessCpuTime != null && Modifier.isPublic(met_getProcessCpuTime.getModifiers())) {
            try {
                currElapsedCPUTime = (Long) met_getProcessCpuTime.invoke(osmx);
                currElapsedRealTime = System.nanoTime();

                long d1 = currElapsedRealTime - lastElapsedRealTime;
                long d2 = currElapsedCPUTime - lastElapsedCPUTime;
                cpuUsage = (double) d2 / d1;
                int processors = osmx.getAvailableProcessors();
                cpuUsage = (cpuUsage / processors) * cpuNSFactor * 100;
            } catch (IllegalArgumentException e) {
                cpuUsage = -1;
                FFDCFilter.processException(e, getClass().getName(), "getCPU");
            } catch (IllegalAccessException e) {
                cpuUsage = -1;
                FFDCFilter.processException(e, getClass().getName(), "getCPU");
            } catch (InvocationTargetException e) {
                cpuUsage = -1;
                FFDCFilter.processException(e, getClass().getName(), "getCPU");
            }
            lastElapsedRealTime = currElapsedRealTime;
            lastElapsedCPUTime = currElapsedCPUTime;
        } else {
            cpuUsage = -1;
        }
        return cpuUsage;
    }
}
