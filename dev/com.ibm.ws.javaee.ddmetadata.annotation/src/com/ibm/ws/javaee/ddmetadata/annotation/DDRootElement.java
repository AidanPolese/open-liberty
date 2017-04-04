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
 * This annotation can be added to an interface to describe the root element
 * for an EE deployment descriptor, IBM XML binding file, or IBM XML extension
 * file. All methods in the interface must be annotated with one of:
 * <ul>
 * <li>{@link DDAttribute}</li>
 * <li>{@link DDElement}</li>
 * </ul>
 * 
 * A processor can use this metadata to generate implementation classes for the
 * interface and all directly or indirectly referenced interfaces.
 * Additionally, a process can generate a parser class for the document.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DDRootElement {
    /**
     * The name of the root XML element (e.g., "web-app").
     */
    String name();

    /**
     * The versions of the document supported.
     */
    DDVersion[] versions();
}
