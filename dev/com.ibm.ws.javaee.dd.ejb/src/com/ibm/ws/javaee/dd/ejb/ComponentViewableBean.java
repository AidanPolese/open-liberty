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
 * Represents common elements for beans that support component views.
 */
public interface ComponentViewableBean
                extends EnterpriseBean
{
    /**
     * @return &lt;home>, or null if unspecified
     */
    String getHomeInterfaceName();

    /**
     * @return &lt;remote>, or null if unspecified
     */
    String getRemoteInterfaceName();

    /**
     * @return &lt;local-home>, or null if unspecified
     */
    String getLocalHomeInterfaceName();

    /**
     * @return &lt;local>, or null if unspecified
     */
    String getLocalInterfaceName();
}
