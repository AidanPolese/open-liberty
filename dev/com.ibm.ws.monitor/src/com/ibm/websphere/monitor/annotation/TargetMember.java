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
 * Used to annotate a method parameter of type {@link java.lang.reflect.Method}, {@link java.lang.reflect.Constructor}, {@link java.lang.reflect.Field}, or
 * {@link java.lang.reflect.Member}.
 * When the method is invoked, the {@code Member} associated with target
 * member of a field access or method call probe will be passed to the
 * method via the annotated argument.
 * <p>
 * When the argument type is not valid for the probe, a {@code null} reference will be used. This can happen, for example, when the argument
 * is declared as a {@code java.lang.reflect.Method} and the probe source
 * was actually a {@code java.lang.reflect.Constructor}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TargetMember {

}
