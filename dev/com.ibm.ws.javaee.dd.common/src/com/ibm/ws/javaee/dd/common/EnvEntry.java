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
 * Represents &lt;env-entry>.
 */
public interface EnvEntry
                extends ResourceGroup, Describable
{
    /**
     * @return &lt;env-entry-type>, or null if unspecified
     */
    String getTypeName();

    /**
     * @return &lt;env-entry-value>, or null if unspecified
     */
    String getValue();
}
