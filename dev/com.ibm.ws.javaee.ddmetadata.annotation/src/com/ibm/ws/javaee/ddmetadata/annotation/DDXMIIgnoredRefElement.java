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
 * Metadata for an ignored reference element in an XMI document.
 * 
 * @see DDXMIRefElement
 */
@Target({})
public @interface DDXMIIgnoredRefElement {
    /**
     * The element name.
     */
    String name();
}
