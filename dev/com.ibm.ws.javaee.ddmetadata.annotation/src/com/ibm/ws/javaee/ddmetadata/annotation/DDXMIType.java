/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmetadata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be added to an interface for an element to indicate that
 * the {@code xmi:type} attribute should be allowed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DDXMIType {
    /**
     * The type name. For {@code xmi:type="pfx:TypeName"}, this should be {@code TypeName}.
     */
    String[] name();

    /**
     * The type namespace. For {@code xmi:type="pfx:TypeName"}, this should be
     * the value of {@code xmlns:pfx="..."}.
     */
    String namespace();
}
