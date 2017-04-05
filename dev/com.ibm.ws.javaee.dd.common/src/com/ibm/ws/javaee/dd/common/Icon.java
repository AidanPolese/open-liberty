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
 * Represents &lt;icon>.
 */
public interface Icon
{
    /**
     * @return &lt;small-icon>, or null if unspecified
     */
    String getSmallIcon();

    /**
     * @return &lt;large-icon>, or null if unspecified
     */
    String getLargeIcon();

    /**
     * @return xml:lang, or null if unspecified
     */
    String getLang();
}
