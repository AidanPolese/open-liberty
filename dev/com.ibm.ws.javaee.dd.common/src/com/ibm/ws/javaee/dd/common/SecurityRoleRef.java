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
 * Represents &lt;security-role-ref>.
 */
public interface SecurityRoleRef
                extends Describable
{
    /**
     * @return &lt;role-name>
     */
    String getName();

    /**
     * @return &lt;role-link>, or null if unspecified
     */
    String getLink();
}
