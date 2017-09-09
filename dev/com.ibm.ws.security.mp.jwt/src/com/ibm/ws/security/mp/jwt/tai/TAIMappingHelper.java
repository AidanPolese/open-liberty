/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt.tai;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.security.auth.Subject;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.ws.security.authentication.AuthenticationConstants;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtConfig;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.security.mp.jwt.impl.utils.JwtPrincipalMapping;
import com.ibm.wsspi.security.token.AttributeNameConstants;

public class TAIMappingHelper {

    private static TraceComponent tc = Tr.register(TAIMappingHelper.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    @Sensitive
    String decodedTokenPayload = null;
    String username = null;
    JwtPrincipalMapping claimToPrincipalMapping = null;
    MicroProfileJwtConfig config = null;
    JsonWebToken jwtPrincipal = null;
    Hashtable<String, Object> customProperties = new Hashtable<String, Object>();

    TAIJwtUtils taiJwtUtils = new TAIJwtUtils();

    public TAIMappingHelper(@Sensitive String decodedPayload, MicroProfileJwtConfig clientConfig) throws MpJwtProcessingException {
        decodedTokenPayload = decodedPayload;
        config = clientConfig;
        if (decodedTokenPayload != null) {
            claimToPrincipalMapping = new JwtPrincipalMapping(decodedTokenPayload, config.getUserNameAttribute(), config.getGroupNameAttribute(), false);
            setUsername();
        }
    }

    public void createJwtPrincipalAndPopulateCustomProperties(@Sensitive JwtToken jwtToken) throws MpJwtProcessingException {
        jwtPrincipal = createJwtPrincipal(jwtToken);
        String issuer = getIssuer(jwtToken);
        if (issuer != null) {
            customProperties = populateCustomProperties(issuer);
        }
    }

    public Subject createSubjectFromCustomProperties(@Sensitive JwtToken jwt) {
        Subject subject = new Subject();
        //        if (jwt != null) {
        //            subject.getPrivateCredentials().add(jwt);
        //        }
        customProperties.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, username);
        customProperties.put(AuthenticationConstants.INTERNAL_JSON_WEB_TOKEN, jwtPrincipal);

        subject.getPrivateCredentials().add(jwtPrincipal);
        subject.getPrivateCredentials().add(customProperties);
        return subject;
    }

    public String getUsername() {
        return username;
    }

    public Hashtable<String, Object> getCustomProperties() {
        return customProperties;
    }

    public JsonWebToken getJwtPrincipal() {
        return jwtPrincipal;
    }

    void setUsername() throws MpJwtProcessingException {
        if (claimToPrincipalMapping != null) {
            username = claimToPrincipalMapping.getMappedUser();
        }
        if (username == null) {
            String msg = Tr.formatMessage(tc, "USERNAME_NOT_FOUND");
            Tr.error(tc, msg);
            throw new MpJwtProcessingException(msg);
        }
    }

    JsonWebToken createJwtPrincipal(@Sensitive JwtToken jwtToken) {
        if (claimToPrincipalMapping == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Claim to principal mapping object not initialized");
            }
            return null;
        }
        return taiJwtUtils.createJwtPrincipal(username, claimToPrincipalMapping.getMappedGroups(), jwtToken);
    }

    String getIssuer(@Sensitive JwtToken jwtToken) throws MpJwtProcessingException {
        if (decodedTokenPayload == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Token payload is null");
            }
            return null;
        }
        try {
            return (String) JsonUtils.claimFromJsonObject(decodedTokenPayload, Claims.ISSUER);
        } catch (Exception e) {
            String msg = Tr.formatMessage(tc, "CANNOT_GET_CLAIM_FROM_JSON", new Object[] { Claims.ISSUER, e.getLocalizedMessage() });
            Tr.error(tc, msg);
            throw new MpJwtProcessingException(msg);
        }
    }

    Hashtable<String, Object> populateCustomProperties(String issuer) {
        Hashtable<String, Object> customProperties = new Hashtable<String, Object>();

        String realm = getRealm(issuer);
        String uniqueID = getUniqueId(realm);
        List<String> groupswithrealm = getGroupsWithRealm(realm);

        customProperties.put(AttributeNameConstants.WSCREDENTIAL_UNIQUEID, uniqueID);
        if (realm != null && !realm.isEmpty()) {
            customProperties.put(AttributeNameConstants.WSCREDENTIAL_REALM, realm);
        }
        if (!groupswithrealm.isEmpty()) {
            customProperties.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groupswithrealm);
        }
        return customProperties;
    }

    String getRealm(String issuer) {
        // Default realm to the issuer
        String realm = issuer;

        //        if (realm == null) {
        //            // runtime default
        //            realm = defaultRealm(clientConfig);
        //        }
        //        if (realm == null) {
        //            Tr.error(tc, "REALM_NOT_FOUND", new Object[] {});
        //            return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
        //        }
        return realm;
    }

    String getUniqueId(String realm) {
        String uniqueUser = null;
        if (claimToPrincipalMapping != null) {
            uniqueUser = claimToPrincipalMapping.getMappedUser();
        }
        return new StringBuffer("user:").append(realm).append("/").append(uniqueUser).toString();
    }

    List<String> getGroupsWithRealm(String realm) {
        List<String> groups = null;
        if (claimToPrincipalMapping != null) {
            groups = claimToPrincipalMapping.getMappedGroups();
        }
        List<String> groupsWithRealm = new ArrayList<String>();
        if (groups != null) {
            for (String groupEntry : groups) {
                String group = new StringBuffer("group:").append(realm).append("/").append(groupEntry).toString();
                groupsWithRealm.add(group);
            }
        }
        return groupsWithRealm;
    }

}
