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
 * Metadata for an XML element that belongs to the same list as another element.
 */
@Target({})
public @interface DDChoiceElement {
    /**
     * The element name.
     */
    String name();

    /**
     * The subtype for this element. This type must extend the return type of
     * the method annotated with {@link DDChoiceElements}.
     */
    Class<?> type();
}
