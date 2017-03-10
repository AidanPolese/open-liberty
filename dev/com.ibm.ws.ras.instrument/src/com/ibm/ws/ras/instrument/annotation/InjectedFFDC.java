/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ras.instrument.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that FFDC has been injected into a method by a
 * processor in order to avoid double processing. This annotation is not
 * intended to be added manually.
 * <p>
 * This annotation differs from @InjectedTrace because of retention policy.
 * We do not perform dynamic FFDC instrumentation, so we do not need annotations
 * to be visible at runtime, which reduces Java heap overhead.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface InjectedFFDC {}
