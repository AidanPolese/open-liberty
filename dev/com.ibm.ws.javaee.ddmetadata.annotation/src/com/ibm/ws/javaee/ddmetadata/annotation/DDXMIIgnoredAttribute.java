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

import java.lang.annotation.Target;

/**
 * Metadata for an ignored attribute in an XMI document.
 *
 * @see DDXMIAttribute
 */
@Target({})
public @interface DDXMIIgnoredAttribute {
    /**
     * The attribute name.
     */
    String name();

    /**
     * The attribute type.
     */
    DDAttributeType type();

    /**
     * The enumeration constants if type is {@link DDAttributeType#Enum}.
     */
    String[] enumConstants() default {};

    /**
     * True if an attribute can be represented as a nested element with the
     * xsi:nil="true" attribute.
     */
    boolean nillable() default false;
}
