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

import java.lang.reflect.Method;

/**
 * Template for a proxy implementation that notifies the monitoring
 * code where a new class has been initialized.
 */
public class ClassAvailableProxy {

    /**
     * Object instance that handles processing of probe candidates.
     */
    private static Object classAvailableTarget;

    /**
     * Method on {@link #classAvailableTarget} that implements the {@link #classAvailable} method.
     */
    private static Method classAvailableMethod;

    /**
     * Setup the proxy target.
     * 
     * @param target the object instance to call
     * @param method the method to invoke
     */
    final static void setClassAvailableTarget(Object target, Method method) {
        classAvailableTarget = target;
        classAvailableMethod = method;
    }

    /**
     * Notify the monitoring {@code classAvailable} target that a new class
     * instance is available.
     * 
     * @param clazz the initialized {@link Class}
     */
    public final static void classAvailable(Class<?> clazz) {
        Object target = classAvailableTarget;
        Method method = classAvailableMethod;
        if (target == null || method == null) {
            return;
        }
        try {
            method.invoke(target, clazz);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
