/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

/**
 * Represents &lt;activation-config-property>.
 */
public interface ActivationConfigProperty
{
    /**
     * @return &lt;activation-config-property-name>
     */
    String getName();

    /**
     * @return &lt;activation-config-property-value>
     */
    String getValue();
}
