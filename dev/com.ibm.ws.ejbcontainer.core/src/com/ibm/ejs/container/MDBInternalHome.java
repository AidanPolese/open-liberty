/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2006, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This interface is used to provide the methods that are
 * unique to a MDB home bean objects and is to be used only
 * internally by the EJB container component.
 */
public interface MDBInternalHome
{
    /**
     * Activate the home for a MDB so that it can receive messages
     * from a message provider.
     */
    void activateEndpoint()
                    throws Exception;

    /**
     * Deactivate a previously activated MDB home.
     */
    void deactivateEndpoint()
                    throws Exception;
}
