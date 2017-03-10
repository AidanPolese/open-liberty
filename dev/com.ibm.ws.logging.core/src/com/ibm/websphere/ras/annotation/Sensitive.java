/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2014
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.ras.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the return type from a method or a parameter to a method is
 * considered &quot;sensitive&quot; and should not be traced via the trace
 * instrumentation. Instead of invoking the <code>toString</code> method on the
 * type, only the class name and hash code or the primitive type identifier will
 * be traced.
 * <p>
 * For example:<br>
 * 
 * <pre>
 * &#64;Sensitive
 * public String normalizePassword(&#64;Sensitive String password) {
 * &#32;&#32;if (password == null) {
 * &#32;&#32;&#32;&#32;return "";
 * &#32;&#32;}
 * &#32;&#32;return password;
 * }
 * </pre>
 * 
 * will trace the input password as something like
 * <code>&lt;sensitive java.lang.String@abcd1234&gt;</code> if the input
 * password was non-null or <code>null</code> if it was null. The same pattern
 * will be applied to return values.
 * <p>
 * Additionally, this annotation can be used on sensitive types and fields to
 * prevent them from being introspected during FFDC.
 */
@Target({ CONSTRUCTOR, FIELD, METHOD, PARAMETER, TYPE })
@Retention(RUNTIME)
public @interface Sensitive {}
