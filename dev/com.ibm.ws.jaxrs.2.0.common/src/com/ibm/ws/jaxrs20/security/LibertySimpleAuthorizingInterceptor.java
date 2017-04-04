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
 * 
 * This class is mainly used to deal with basic authentication:
 * @DenyAll
 * @PermitAll
 * @RolesAllowed
 * You need to define roles in server.xml and map it in web.xml
 * Annotated constraints are additional to any configured security constraints. 
 * The JAX-RS runtime environment checks for annotated constraints after 
 * the web container runtime environment has checked for security constraints that are configured in the web.xml file.
 * Details in: 
 * http://www-01.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/twbs_jaxrs_impl_securejaxrs_annotations.html?cp=SSAW57_8.5.5%2F1-3-0-40-4-0&lang=en
 */
package com.ibm.ws.jaxrs20.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.AbstractAuthorizingInInterceptor;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class LibertySimpleAuthorizingInterceptor extends
                AbstractAuthorizingInInterceptor {
    private static final TraceComponent tc = Tr
                    .register(LibertySimpleAuthorizingInterceptor.class);

    @Override
    public void handleMessage(Message message) throws Fault {
        SecurityContext sc = message.get(SecurityContext.class);
        if (sc != null) {
            Method method = getTargetMethod(message);
            if (parseMethodSecurity(method, sc)) {
                return;
            }
        }
        throw new AccessDeniedException("Unauthorized");
    }

    private boolean parseMethodSecurity(Method method, SecurityContext sc) {

        boolean denyAll = getDenyAll(method);
        if (denyAll) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Found DenyAll for method: {} " + method.getName()
                             + ", Injection Processing for web service is ignored");
            }
            // throw new WebApplicationException(Response.Status.FORBIDDEN);
            return false;

        } else { // try RolesAllowed

            RolesAllowed rolesAllowed = getRolesAllowed(method);
            if (rolesAllowed != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(
                             tc,
                             "found RolesAllowed in method: {} "
                                             + method.getName());
                }
                String[] theseroles = rolesAllowed.value();

                if (!isUserInRole(sc, Arrays.asList(theseroles), false)) {
                    return false;
                }
                return true;

            } else {
                boolean permitAll = getPermitAll(method);
                if (permitAll) {
                    if (TraceComponent.isAnyTracingEnabled()
                        && tc.isDebugEnabled()) {
                        Tr.debug(
                                 tc,
                                 "Found PermitAll for method: {}"
                                                 + method.getName());
                    }
                    return true;
                } else { // try class level annotations
                    Class<?> cls = method.getDeclaringClass();
                    return parseClassSecurity(cls, sc);
                }
            }
        }

    } // end parseMethodSecurity

    // parse security JSR250 annotations at the class level
    private boolean parseClassSecurity(Class<?> cls, SecurityContext sc) {

        // try DenyAll
        DenyAll denyAll = cls.getAnnotation(DenyAll.class);
        if (denyAll != null) {
            return false;
        } else { // try RolesAllowed

            RolesAllowed rolesAllowed = cls.getAnnotation(RolesAllowed.class);
            if (rolesAllowed != null) {

                String[] theseroles = rolesAllowed.value();
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(
                             tc,
                             "found RolesAllowed in class level: {} "
                                             + cls.getName());
                }
                if (!isUserInRole(sc, Arrays.asList(theseroles), false)) {
                    return false;
                }
                return true;
            } else { // try PermitAll
//				PermitAll permitAll = cls.getAnnotation(PermitAll.class);
                return true;
            }

        }

    } // end parseClassSecurity

    private RolesAllowed getRolesAllowed(Method method) {
        return method.getAnnotation(RolesAllowed.class);
    }

    private boolean getPermitAll(Method method) {
        return method.isAnnotationPresent(PermitAll.class);
    }

    private boolean getDenyAll(Method method) {
        return method.isAnnotationPresent(DenyAll.class);
    }

    @Override
    protected List<String> getExpectedRoles(Method method) {
        // TODO Auto-generated method stub
        return null;
    }

}
