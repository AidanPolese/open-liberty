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
 * Represents the descriptionType type from the javaee XSD.
 */
public interface Description
{
    /**
     * @return xml:lang, or null if unspecified
     */
    String getLang();

    /**
     * @return the description
     */
    String getValue();
}
