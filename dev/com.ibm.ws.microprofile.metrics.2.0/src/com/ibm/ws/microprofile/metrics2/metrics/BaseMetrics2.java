/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.metrics2.metrics;

import java.util.HashSet;
import java.util.Set;

import javax.management.MBeanServer;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;

import com.ibm.ws.microprofile.metrics.BaseMetricConstants;
import com.ibm.ws.microprofile.metrics.BaseMetrics;
import com.ibm.ws.microprofile.metrics.impl.SharedMetricRegistries;

public class BaseMetrics2 extends BaseMetrics {
    private static BaseMetrics baseMetrics = null;
    private static String BASE = MetricRegistry.Type.BASE.getName();
    MBeanServer mbs;
    private static Set<String> gcObjectNames = new HashSet<String>();

    private static SharedMetricRegistries SHARED_METRIC_REGISTRY;

    public static synchronized BaseMetrics getInstance(SharedMetricRegistries sharedMetricRegistry) {
        SHARED_METRIC_REGISTRY = sharedMetricRegistry;
        if (baseMetrics == null)
            baseMetrics = new BaseMetrics2();
        return baseMetrics;
    }

    @Override
    public void createBaseMetrics() {
        MetricRegistry registry = SHARED_METRIC_REGISTRY.getOrCreate(BASE);
        //MEMORY METRICS
        registry.register("memory.usedHeap", new BMGauge<Number>(BaseMetricConstants.MEMORY_OBJECT_TYPE, "HeapMemoryUsage", "used"),
                          Metadata.builder().withName("memory.usedHeap").withDisplayName("Used Heap Memory").withDescription("memory.committedHeap.description").withType(MetricType.GAUGE).withUnit(MetricUnits.BYTES).build());

        registry.register("memory.committedHeap", new BMGauge<Number>(BaseMetricConstants.MEMORY_OBJECT_TYPE, "HeapMemoryUsage", "committed"),
                          Metadata.builder().withName("memory.committedHeap").withDisplayName("Committed Heap Memory").withDescription("memory.committedHeap.description").withType(MetricType.GAUGE).withUnit(MetricUnits.BYTES).build());

        registry.register("memory.maxHeap", new BMGauge<Number>(BaseMetricConstants.MEMORY_OBJECT_TYPE, "HeapMemoryUsage", "max"),
                          Metadata.builder().withName("memory.maxHeap").withDisplayName("Max Heap Memory").withDescription("memory.maxHeap.description").withType(MetricType.GAUGE).withUnit(MetricUnits.BYTES).build());

        //JVM METRICS
        registry.register("jvm.uptime", new BMGauge<Number>(BaseMetricConstants.RUNTIME_OBJECT_TYPE, "Uptime"),
                          Metadata.builder().withName("jvm.uptime").withDisplayName("JVM Uptime").withDescription("jvm.uptime.description").withType(MetricType.GAUGE).withUnit(MetricUnits.MILLISECONDS).build());

        //THREAD JVM -
        registry.register("thread.count", new BMCounter(BaseMetricConstants.THREAD_OBJECT_TYPE, "ThreadCount"),
                          Metadata.builder().withName("thread.count").withDisplayName("Thread Count").withDescription("thread.count.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

        registry.register("thread.daemon.count", new BMCounter(BaseMetricConstants.THREAD_OBJECT_TYPE, "DaemonThreadCount"),
                          Metadata.builder().withName("thread.daemon.count").withDisplayName("Daemon Thread Count").withDescription("thread.daemon.count.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

        registry.register("thread.max.count", new BMCounter(BaseMetricConstants.THREAD_OBJECT_TYPE, "PeakThreadCount"),
                          Metadata.builder().withName("thread.max.count").withDisplayName("Peak Thread Count").withDescription("thread.max.count.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

        //CLASSLOADING METRICS
        registry.register("classloader.currentLoadedClass.count", new BMCounter(BaseMetricConstants.CLASSLOADING_OBJECT_TYPE, "LoadedClassCount"),
                          Metadata.builder().withName("classloader.currentLoadedClass.count").withDisplayName("Current Loaded Class Count").withDescription("classloader.currentLoadedClass.count.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

        registry.register("classloader.totalLoadedClass.count", new BMCounter(BaseMetricConstants.CLASSLOADING_OBJECT_TYPE, "TotalLoadedClassCount"),
                          Metadata.builder().withName("classloader.totalLoadedClass.count").withDisplayName("Total Loaded Class Count").withDescription("classloader.totalLoadedClass.count.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

        registry.register("classloader.totalUnloadedClass.count", new BMCounter(BaseMetricConstants.CLASSLOADING_OBJECT_TYPE, "UnloadedClassCount"),
                          Metadata.builder().withName("classloader.totalUnloadedClass.count").withDisplayName("Total Unloaded Class Count").withDescription("classloader.totalUnloadedClass.count.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

        //OPERATING SYSTEM
        registry.register("cpu.availableProcessors", new BMGauge<Number>(BaseMetricConstants.OS_OBJECT_TYPE, "AvailableProcessors"),
                          Metadata.builder().withName("cpu.availableProcessors").withDisplayName("Available Processors").withDescription("cpu.availableProcessors.description").withType(MetricType.GAUGE).withUnit(MetricUnits.NONE).build());

        registry.register("cpu.systemLoadAverage", new BMGauge<Number>(BaseMetricConstants.OS_OBJECT_TYPE, "SystemLoadAverage"),
                          Metadata.builder().withName("cpu.systemLoadAverage").withDisplayName("System Load Average").withDescription("cpu.systemLoadAverage.description").withType(MetricType.GAUGE).withUnit(MetricUnits.NONE).build());

        registry.register("cpu.processCpuLoad", new BMGauge<Number>(BaseMetricConstants.OS_OBJECT_TYPE, "ProcessCpuLoad"),
                          Metadata.builder().withName("cpu.processCpuLoad").withDisplayName("Process CPU Load").withDescription("cpu.processCpuLoad.description").withType(MetricType.GAUGE).withUnit(MetricUnits.PERCENT).build());

        //GARBAGE COLLECTOR METRICS
        for (String gcName : gcObjectNames) {

            String gcNameNoSpace = removeSpaces(gcName);

            //gc.%s.count
            String nameToRegister = "gc." + gcNameNoSpace + ".count";
            registry.register(nameToRegister, new BMCounter(BaseMetricConstants.GC_OBJECT_TYPE_NAME + gcName, "CollectionCount"),
                              Metadata.builder().withName(nameToRegister).withDisplayName("Garbage Collection Count").withDescription("garbageCollectionCount.description").withType(MetricType.COUNTER).withUnit(MetricUnits.NONE).build());

            //gc.%s.time
            nameToRegister = "gc." + gcNameNoSpace + ".time";
            registry.register(nameToRegister, new BMGauge<Number>(BaseMetricConstants.GC_OBJECT_TYPE_NAME + gcName, "CollectionTime"),
                              Metadata.builder().withName(nameToRegister).withDisplayName("Garbage Collection Time").withDescription("garbageCollectionTime.description").withType(MetricType.GAUGE).withUnit(MetricUnits.MILLISECONDS).build());

        }

    }

    private String removeSpaces(String aString) {
        return aString.replaceAll("\\s+", "");
    }

}