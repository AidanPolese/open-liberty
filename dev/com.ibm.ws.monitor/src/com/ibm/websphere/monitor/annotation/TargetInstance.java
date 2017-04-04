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
 * Used to annotate a method parameter that is assignment compatible with
 * the type of the target instance of a field access or method call probe event.
 * If the target of the call or owner of the field is incompatible with the
 * declared parameter type, a {@code null} reference will be used. If the target
 * member is a static method or field, the {@link java.lang.Class} declaring the
 * method will be used as the source.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TargetInstance {}
