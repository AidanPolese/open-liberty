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
 * Metadata for an XMI element that produces a value for the annotated method by
 * referring to an element in another document. The type of the other document
 * is specified by {@link DDXMIRootElement#refElementType}. This annotation can
 * only be used with {@link DDAttribute}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface DDXMIRefElement {
    /**
     * The element name.
     */
    String name();

    /**
     * The type of the element in the referenced document.
     */
    Class<?> referentType();

    /**
     * The method name on {@link #referentType} that produces the value.
     */
    String getter();
}
