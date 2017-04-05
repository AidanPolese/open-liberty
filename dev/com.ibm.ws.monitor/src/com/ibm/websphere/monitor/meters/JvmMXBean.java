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
package com.ibm.websphere.monitor.meters;

/**
 * This is a monitor interface for WebSphere JVM Process.
 * <p>
 * Each WebSphere Application Server (Liberty Profile) instance would have one
 * JVM MXBean.<p>
 * The ObjectName for identifying JVM MXBean is:
 * <p>
 * <b>
 * WebSphere:type=JVM.PerformanceData
 * </b>
 * <p>
 * <br>
 * <br>
 * <br>
 * This MXBean is responsible for reporting performance of JVM.
 * Following attributes are available for JVM.
 * 
 * Heap Information <p>
 * - FreeMemory<p>
 * - UsedMemory<p>
 * - Heap<p>
 * <br>
 * 
 * 
 * CPU Information<p>
 * - ProcessCPU<p>
 * <br>
 * 
 * Garbage Collection Information<p>
 * - GCCount<p>
 * - GCTime<p>
 * <br>
 * 
 * JVM Information<p>
 * - UpTime<p>
 * <br>
 * 
 * <p><p>
 * 
 * 
 * 
 * 
 */
public interface JvmMXBean extends com.ibm.websphere.monitor.jmx.JvmMXBean {

}
