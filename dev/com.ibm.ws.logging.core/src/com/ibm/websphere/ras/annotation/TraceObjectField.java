/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2013
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.ras.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation enables dynamic trace processing and specifies the static
 * trace object field (Logger or TraceComponent) that should be used. This
 * annotation is not intended to be added manually.
 */
@Retention(RUNTIME)
@Target({ TYPE })
public @interface TraceObjectField {
    /**
     * The name of the Logger or TraceComponent static field.
     */
    String fieldName() default "";

    /**
     * The descriptor of the field named by {@link #fieldName} in JVM format
     * (for example, "Lcom/ibm/websphere/ras/TraceComponent;").
     */
    String fieldDesc() default "";
}
