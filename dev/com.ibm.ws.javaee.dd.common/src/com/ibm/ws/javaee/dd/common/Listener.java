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
 * Represents the listenerType type from the javaee XSD.
 */
public interface Listener
                extends DescriptionGroup
{
    /**
     * @return &lt;listener-class>
     */
    String getListenerClassName();
}
