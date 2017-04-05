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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A {@code Probe} contains information about a probe attach site that has
 * been injected by the monitoring runtime. Monitors callbacks that are
 * associated with a probe will be called with a reference to the {@code Probe}.
 */
public interface Probe {

    /**
     * Get the probe name. This name will indicate the class, method,
     * and probed method but will not include filter criteria.
     * 
     * @return the probe name
     */
    String getName();

    /**
     * Get a the {@link java.lang.Class} firing the probe.
     * 
     * @return the class firing the probe
     */
    Class<?> getSourceClass();

    /**
     * Get a reference to the {@link java.lang.reflect.Method} firing the
     * probe.
     * 
     * @return the method firing the probe or {@code null} for constructors
     */
    Method getSourceMethod();

/**
     * Get a reference to the {@link java.lang.reflect.Constructor) firing
     * the probe.
     *
     * @return the constructor firing the probe or {@code null} for methods
     *     other than the constructor
     */
    Constructor<?> getSourceConstructor();

    /**
     * Get the OSGi bundle identifier associated with the probed class or {@code -1} if the class was not loaded from an OSGi bundle.
     * 
     * @return the OSGi bundle identifier for the bundle owning the probed
     *         class
     */
    long getSourceBundleId();

    //    Class<?> getTargetClass();
    //    
    //    Method getTargetMethod();
    //    
    //    Constructor<?> getTargetConstructor();
    //
    //    Field getTargetField();

}
