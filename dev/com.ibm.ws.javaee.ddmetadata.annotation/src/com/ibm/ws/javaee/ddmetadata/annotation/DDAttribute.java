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
 * Metadata for an XML attribute.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface DDAttribute {
    /**
     * The attribute name.
     */
    String name();

    /**
     * The intermediate element name if the attribute exists on a nested element
     * that is not mirrored in the interfaces.
     */
    String elementName() default "";

    /**
     * The attribute type.
     */
    DDAttributeType type();

    /**
     * True if the parser should fail if an attribute is not specified.
     */
    boolean required() default false;

    /**
     * The default value. If not specified, a type-specific default (typically
     * null) will be used.
     */
    String defaultValue() default "";
}
