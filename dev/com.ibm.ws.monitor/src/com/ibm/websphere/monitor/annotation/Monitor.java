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

package com.ibm.websphere.monitor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Monitor {

    /**
     * The name of the monitor. If not specified, the name of the class
     * implementing the monitor will be used as the name.
     */
    String name() default "";

    /**
     * The names of the groups that this monitor is a member of. Groups may
     * be used to enable or disable a set of related monitors.
     */
    String[] group() default {};

    /**
     * Indicates whether or not the monitor is enabled when the host bundle
     * starts. A monitor is enabled by default.
     */
    boolean enabled() default true;

}
