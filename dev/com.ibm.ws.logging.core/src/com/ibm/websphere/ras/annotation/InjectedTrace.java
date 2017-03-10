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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation indicates that trace has been injected into a method by a
 * processor in order to avoid double processing. This annotation is not
 * intended to be added manually.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface InjectedTrace {
    /**
     * The processing that has been performed. Each element should be a
     * fully-qualified class name.
     */
    String[] value() default {};
}
