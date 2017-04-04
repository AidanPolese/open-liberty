/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

/**
 * Represents all java:comp-related subelements from the
 * jndiEnvironmentRefsGroup XSD type.
 */
public interface JNDIEnvironmentRef
{
    /**
     * @return the name relative to java:comp/env (&lt;env-entry-name>,
     *         &lt;ejb-ref-name>, etc.)
     */
    String getName();
}
