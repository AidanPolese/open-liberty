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
package com.ibm.ws.container.service.security.internal;

import java.util.Collections;
import java.util.List;

import com.ibm.ws.container.service.security.SecurityRoles;
import com.ibm.ws.javaee.dd.appbnd.ApplicationBnd;
import com.ibm.ws.javaee.dd.appbnd.SecurityRole;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public class SecurityRolesAdapter implements ContainerAdapter<SecurityRoles> {

    @Override
    public SecurityRoles adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {

        ApplicationBnd appBnd = containerToAdapt.adapt(ApplicationBnd.class);

        List<SecurityRole> roles;
        if (appBnd == null)
            roles = Collections.emptyList();
        else
            roles = appBnd.getSecurityRoles();

        return new SecurityRolesImpl(roles);
    }

}
