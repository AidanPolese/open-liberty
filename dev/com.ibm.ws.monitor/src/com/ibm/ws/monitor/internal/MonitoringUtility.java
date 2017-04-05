/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.monitor.internal;

import java.util.Collection;
import java.util.HashSet;

import org.osgi.framework.Bundle;

/**
 *
 */
public class MonitoringUtility {
    public static Collection<Class<?>> loadMonitoringClasses(Bundle bundle) {
        Collection<Class<?>> classes = new HashSet<Class<?>>();
        String header = bundle.getHeaders("").get("Liberty-Monitoring-Components");
        String[] classNames = header.split("[,\\s]");

        for (String className : classNames) {
            className = className.trim();
            if (className.isEmpty())
                continue;
            Class<?> clazz = ReflectionHelper.loadClass(bundle, className);
            if (clazz != null && !classes.contains(clazz)) {
                classes.add(clazz);
            }
        }

        return classes;
    }
}
