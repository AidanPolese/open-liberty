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
 * Metadata for an XMI attribute. This can only be used with {@link DDAttribute}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface DDXMIAttribute {
    /**
     * The attribute name.
     */
    String name();

    /**
     * True if an attribute can be represented as a nested element with the
     * xsi:nil="true" attribute.
     */
    boolean nillable() default false;

    /**
     * The intermediate element name if the attribute exists on a nested element
     * that is not mirrored in the interfaces. As an implementation restriction,
     * this can only be used if the corresponding {@link DDAttribute#elementName} also
     * specifies an intermediate element name.
     */
    String elementName() default "";

    /**
     * The type name in the xmi:type attribute that must be present on the
     * intermediate element. This must be used with {@link #elementXMITypeNamespace}.
     * As an implementation restriction, only one xmi:type can be specified.
     */
    String elementXMIType() default "";

    /**
     * The naamespace of the prefix in the xmi:type that must be present on the
     * intermediate element. This can only be used with {@link #elementXMIType}.
     */
    String elementXMITypeNamespace() default "";
}
