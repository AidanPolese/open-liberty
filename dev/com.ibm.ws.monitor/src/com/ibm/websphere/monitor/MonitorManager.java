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

package com.ibm.websphere.monitor;

import java.util.Map;

public interface MonitorManager {

    public boolean registerMonitor(Object monitor);

    public boolean registerMonitor(Object monitor, Map<String, Object> config);

    public boolean unregisterMonitor(Object monitor);

    //RTCD 89497-Update for non Excluded classes list
    public void updateNonExcludedClassesSet(String className);

    // public <T> T getAttribute(String monitorName, String attributeName);

    //    public boolean enableMonitor(String name);
    //
    //    public boolean isEnabled(String name);
    //
    //    public boolean disableMonitor(String name);

}
