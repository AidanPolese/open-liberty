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
 * Represents common elements between &lt;persistence-context-ref> and
 * &lt;persistence-unit-ref>.
 */
public interface PersistenceRef
                extends ResourceBaseGroup, Describable
{
    /**
     * @return &lt;persistence-unit-name>, or null if unspecified
     */
    String getPersistenceUnitName();
}
