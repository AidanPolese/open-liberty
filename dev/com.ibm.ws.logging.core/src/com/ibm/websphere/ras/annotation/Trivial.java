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

package com.ibm.websphere.ras.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a method or class is trivial to the point where
 * instrumentation with diagnostic trace has no value. When used to annotate a
 * class, all methods in the class are considered trivial. When used to annotate
 * a package, all classes in the package are considered trivial.
 */
@Retention(RUNTIME)
@Target({ CONSTRUCTOR, METHOD, TYPE, PACKAGE })
public @interface Trivial {}