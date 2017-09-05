/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.impl.utils;

import java.security.Principal;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.mp.jwt.proxy.JsonWebTokenUtil;

/**
 * The JsonWebTokenUtilImpl service only activate when running with JDK 8 and it's handler the JsonWebToken in subject
 */

@org.osgi.service.component.annotations.Component(service = JsonWebTokenUtil.class, name = "JsonWebTokenUtil", immediate = true, property = "service.vendor=IBM")
public class JsonWebTokenUtilImpl implements JsonWebTokenUtil {

    public static final TraceComponent tc = Tr.register(JsonWebTokenUtilImpl.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    @Override
    public Principal getJsonWebTokenPrincipal(Subject subject) {

        Set<JsonWebToken> jsonWebTokenPrincipal = subject.getPrincipals(JsonWebToken.class);

        if (jsonWebTokenPrincipal.size() > 1) {
            multipleJsonWebTokenPrincipalsError(jsonWebTokenPrincipal);
        }

        if (!jsonWebTokenPrincipal.isEmpty()) {
            return jsonWebTokenPrincipal.iterator().next();
        }

        return null;
    }

    @Override
    public void addJsonWebToken(Subject subject, Hashtable<String, ?> customProperties, String key) {
        if (customProperties != null) {
            JsonWebToken jsonWebToken = (JsonWebToken) customProperties.get(key);
            if (jsonWebToken != null) {
                subject.getPrincipals().add(jsonWebToken);
            }
        }
    }

    @Override
    public Principal cloneJsonWebToken(Subject subject) {
        return null;
    }

    private static void multipleJsonWebTokenPrincipalsError(Set<JsonWebToken> principals) {
        String principalNames = null;
        for (JsonWebToken principal : principals) {
            if (principalNames == null)
                principalNames = principal.getName();
            else
                principalNames = principalNames + ", " + principal.getName();
        }
        // throw new IllegalStateException(Tr.formatMessage(tc,
        // "SEC_TOO_MANY_PRINCIPALS", principalNames));
    }

    @Activate
    protected void activate(ComponentContext cc) {
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "JsonWebTokenUtil service is activated");
        }
    }

    @Modified
    protected void modified(Map<String, Object> props) {
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "JsonWebTokenUtil service is deactivated");
        }
    }
}
