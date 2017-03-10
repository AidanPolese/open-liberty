/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal;

public enum KernelStartLevel {
    /** WAS Server start levels: 0 (stopped), and 1 have special meaning w/ OSGi */
    OSGI_INIT(1),
    BOOTSTRAP(2),
    KERNEL_CONFIG(3),
    KERNEL(4),
    KERNEL_LATE(6),
    FEATURE_PREPARE(7),
    ACTIVE(20);

    final int startLevel;

    KernelStartLevel(int level) {
        startLevel = level;
    }

    public int getLevel() {
        return startLevel;
    }
}