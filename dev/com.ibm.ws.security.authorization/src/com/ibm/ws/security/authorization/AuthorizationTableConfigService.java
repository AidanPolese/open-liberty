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
package com.ibm.ws.security.authorization;

import java.util.Map;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * This class defines the interface for passing configuration data
 * containing a set of role mappings
 * 
 * @see AuthorizationService
 * @see AuthorizationTableService
 * @see RoleSet
 */
public interface AuthorizationTableConfigService {

    /**
     * Set the role mappings from the config
     * 
     * @param configurationAdmin
     * 
     * @param properties the properties from the pid class activate/modified methods
     */
    void setConfiguration(String[] roleNames, ConfigurationAdmin configurationAdmin, Map<String, Object> properties);
}
