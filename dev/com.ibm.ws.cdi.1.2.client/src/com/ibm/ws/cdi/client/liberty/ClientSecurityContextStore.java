/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.client.liberty;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.ws.cdi.interfaces.SecurityContextStore;

/**
 * Implementation of {@link SecurityContextStore} which can retrieve the current principal when running in the client container.
 */
@Component(
                name = "com.ibm.ws.cdi.ClientSecurityContextStore",
                immediate = true,
                property = { "service.vendor=IBM" })
public class ClientSecurityContextStore implements SecurityContextStore {

    private static final TraceComponent tc = Tr.register(ClientSecurityContextStore.class);

    /** {@inheritDoc} */
    @Override
    public Principal getCurrentPrincipal() {
        Principal principal = null;

        // Get hold of the current subject
        Subject subject = null;
        try {
            subject = WSSubject.getCallerSubject();
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Current subject: ", subject);
            }
        } catch (WSSecurityException e) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Failed to get current subject", e);
            }
        }

        // If we have a subject, extract the first principal
        if (subject != null) {
            Set<Principal> principals = subject.getPrincipals();
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Number of principals: ", principals.size());
            }
            if (!principals.isEmpty()) {
                principal = principals.iterator().next();
            }
        }

        return principal;
    }

}
