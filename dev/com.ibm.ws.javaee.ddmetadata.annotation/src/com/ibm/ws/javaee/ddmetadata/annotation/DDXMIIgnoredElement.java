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
 * Metadata for an ignored element in an XMI document.
 * 
 * @see DDXMIElement
 */
@Target({})
public @interface DDXMIIgnoredElement {
    /**
     * The element name
     */
    String name();

    /**
     * True if the element can occur multiple times.
     */
    boolean list() default false;

    /**
     * The child attributes of the ignored element.
     */
    DDXMIIgnoredAttribute[] attributes() default {};

    /**
     * The child reference elements of the ignored element.
     */
    DDXMIIgnoredRefElement[] refElements() default {};
}
