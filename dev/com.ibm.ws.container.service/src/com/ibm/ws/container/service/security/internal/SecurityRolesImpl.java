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
package com.ibm.ws.container.service.security.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.container.service.security.SecurityRoles;
import com.ibm.ws.javaee.dd.appbnd.SecurityRole;

class SecurityRolesImpl implements SecurityRoles {
    private static final TraceComponent tc = Tr.register(SecurityRolesImpl.class);

    private List<SecurityRole> securityRolesList = null;

    /**
     * @param containerToAdapt
     * @param securityRoles
     */
    public SecurityRolesImpl(List<SecurityRole> allSecurityRoles) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "The security roles from the application bind file and server.xml are: " + allSecurityRoles);
        }

        HashMap<String, SecurityRole> mergedRoles = new HashMap<String, SecurityRole>();
        for (SecurityRole role : allSecurityRoles) {
            // Security roles configured in server.xml always appear after roles from the bindings files, so this will result in
            // server.xml configured entries overriding entries from the bindings files.
            SecurityRole previous = mergedRoles.put(role.getName(), role);
            if (previous != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Overriding security role with name " + previous.getName() + "old: " + previous + "new: " + role);
                }
            }
        }
        this.securityRolesList = new ArrayList<SecurityRole>(mergedRoles.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SecurityRole> getSecurityRoles() {
        return securityRolesList;
    }

}