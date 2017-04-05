/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.interfaces;

import java.security.Principal;

/**
 * Stores security information for multiple applications
 */
public interface SecurityContextStore {

    /**
     * Gets the current caller identity.
     * 
     * @return current caller identity or <code>null</code> if none provided.
     */
    public Principal getCurrentPrincipal();

}
