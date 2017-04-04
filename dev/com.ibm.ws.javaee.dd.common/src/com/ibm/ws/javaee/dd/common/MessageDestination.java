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
 * Represents the &lt;message-destination>.
 */
public interface MessageDestination
                extends DescriptionGroup
{
    /**
     * @return &lt;message-destination-name>
     */
    String getName();

    /**
     * @return &lt;mapped-name>, or null if unspecified
     */
    String getMappedName();

    /**
     * @return &lt;lookup-name>, or null if unspecified
     */
    String getLookupName();
}
