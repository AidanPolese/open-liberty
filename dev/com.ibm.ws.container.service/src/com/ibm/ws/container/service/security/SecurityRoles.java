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
package com.ibm.ws.container.service.security;

import java.util.List;

import com.ibm.ws.javaee.dd.appbnd.SecurityRole;

/**
 *
 */
public interface SecurityRoles {

    /**
     * Gets the merged security role mappings for the application. Roles from
     * server.xml take precedence over roles from any ibm-application-bnd file.
     * 
     * @return The merged security role mappings
     */
    public List<SecurityRole> getSecurityRoles();
}
