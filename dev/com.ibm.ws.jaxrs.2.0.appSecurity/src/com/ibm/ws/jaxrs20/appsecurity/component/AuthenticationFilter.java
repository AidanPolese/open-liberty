/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.appsecurity.component;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;

import com.ibm.ws.webcontainer.osgi.collaborator.CollaboratorHelperImpl;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppSecurityCollaborator;

/**
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    /*
     * (non-Javadoc)
     *
     * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext crCtx) throws IOException {
        SecurityContext secCtx = crCtx.getSecurityContext();
        Principal p = secCtx.getUserPrincipal();
        if (p == null || "UNAUTHENTICATED".equals(p.getName())) {

            Message msg = JAXRSUtils.getCurrentMessage();
            HttpServletRequest req = msg.get(HttpServletRequest.class);
            HttpServletResponse resp = msg.get(HttpServletResponse.class);
            IWebAppSecurityCollaborator collaborator = CollaboratorHelperImpl.getCurrentSecurityCollaborator();
            if (collaborator != null) {
                try {
                    collaborator.authenticate(req, resp);
                } catch (ServletException e) {
                    throw new IOException(e);
                }
            }
        }

    }

}
