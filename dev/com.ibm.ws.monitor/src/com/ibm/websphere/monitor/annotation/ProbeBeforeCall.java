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

/**
 * The {@code ProbeBeforeCall} annotation is used to mark a method as the
 * target of a <em>before method call</em> probe event. This annotation
 * must be used in conjunction with the {@link ProbeSite} to indicate the
 * set of methods that must be probed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProbeBeforeCall {

    /**
     * A filter specification for the class or interface defining the target
     * method.
     */
    String clazz();

    /**
     * A filter specification for the name of the method being called.
     */
    String method() default "*";

    /**
     * A filter specification for the arguments of the method being called.
     */
    String args() default "*";

}
