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
package com.ibm.ws.security.mp.jwt.impl.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.TraceConstants;

/**
 *
 */
public class ClaimsUtils {
    public static final TraceComponent tc = Tr.register(ClaimsUtils.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    public ClaimsUtils() {
    }

    public JwtClaims getJwtClaims(String jwt) throws JoseException {

        String payload = null;
        if (jwt != null) {
            String[] parts = JsonUtils.splitTokenString(jwt);
            if (parts.length > 0) {
                payload = JsonUtils.fromBase64ToJsonString(parts[1]); // payload - claims
            }
        }
        JwtClaims jwtclaims = new JwtClaims();

        if (payload != null) {
            Map<String, Object> payloadClaims = org.jose4j.json.JsonUtil.parseJson(payload);
            Set<Entry<String, Object>> entries = payloadClaims.entrySet();
            Iterator<Entry<String, Object>> it = entries.iterator();
            while (it.hasNext()) {
                Entry<String, Object> entry = it.next();

                String key = entry.getKey();
                Object value = entry.getValue();
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Key : " + key + ", Value: " + value);
                }
                if (key != null && value != null) {
                    jwtclaims.setClaim(key, value);
                }
            }
            jwtclaims.setStringClaim(org.eclipse.microprofile.jwt.Claims.raw_token.name(), jwt);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Key : " + "raw_token" + ", Value: " + "raw_token");
            }
            fixJoseTypes(jwtclaims);
        }

        return jwtclaims;

    }

    /**
     * Convert the types jose4j uses for address, sub_jwk, and jwk
     */
    private void fixJoseTypes(JwtClaims claimsSet) {
        //        if (claimsSet.hasClaim(Claims.address.name())) {
        //            replaceMap(Claims.address.name());
        //        }
        //        if (claimsSet.hasClaim(Claims.jwk.name())) {
        //            replaceMap(Claims.jwk.name());
        //        }
        //        if (claimsSet.hasClaim(Claims.sub_jwk.name())) {
        //            replaceMap(Claims.sub_jwk.name());
        //        }
        if (claimsSet.hasClaim("address")) {
            replaceMap("address", claimsSet);
        }
        if (claimsSet.hasClaim("jwk")) {
            replaceMap("jwk", claimsSet);
        }
        if (claimsSet.hasClaim("sub_jwk")) {
            replaceMap("sub_jwk", claimsSet);
        }
        if (claimsSet.hasClaim("aud")) {
            convertToList("aud", claimsSet);
        }
        if (claimsSet.hasClaim("groups")) {
            convertToList("groups", claimsSet);
        }
    }

    /**
     * Replace the jose4j Map<String,Object> with a JsonObject
     *
     * @param name
     *            - claim name
     * @param claimsSet
     */
    private void replaceMap(String name, JwtClaims claimsSet) {
        try {
            Map<String, Object> map = claimsSet.getClaimValue(name, Map.class);
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
            JsonObject jsonObject = builder.build();
            claimsSet.setClaim(name, jsonObject);
        } catch (MalformedClaimException e) {
            //e.printStackTrace();
        }
    }

    /**
     * @param claimsSet
     * @param string
     */
    @FFDCIgnore({ MalformedClaimException.class })
    private void convertToList(String name, JwtClaims claimsSet) {

        List<String> list = null;
        try {
            list = claimsSet.getStringListClaimValue(name);

        } catch (MalformedClaimException e) {
            //e.printStackTrace();
            try {
                String value = claimsSet.getStringClaimValue(name);
                if (value != null) {
                    list = new ArrayList<String>();
                    list.add(value);
                    claimsSet.setClaim(name, list);
                }
            } catch (MalformedClaimException e1) {

            }
        }
    }

}
