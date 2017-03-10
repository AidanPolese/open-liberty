/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.security;

import java.security.CodeSource;
import java.security.PermissionCollection;

/**
 *
 */
public interface PermissionsCombiner {

    /**
     * Combine the static permissions with the configured permissions
     * 
     * @param staticPolicyPermissions The static permissions.
     * @param codesource The code source to get the combined permissions for.
     * @return The combined permissions.
     */
    PermissionCollection getCombinedPermissions(PermissionCollection staticPolicyPermissions, CodeSource codesource);

}
