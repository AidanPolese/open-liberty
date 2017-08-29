/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.tai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.JwtConsumer;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.security.mp.jwt.impl.DefaultJsonWebTokenImpl;

public class TAIJwtUtils {

    public static final TraceComponent tc = Tr.register(TAIJwtUtils.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);
    public static final String TYPE_JWT_TOKEN = "Json Web Token";

    protected TAIJwtUtils() {
    }

    public JwtToken validateMpJwtToken(@Sensitive String idToken, String jwtConfigId) throws MpJwtProcessingException {

        try {
            return JwtConsumer.create(jwtConfigId).createJwt(idToken);
        } catch (Exception e) {
            throw new MpJwtProcessingException("FAILED_TO_CREATE_JWT_FROM_ID_TOKEN", e, new Object[] { jwtConfigId, e.getMessage() });
        }
    }

    /**
     * @param username
     * @param groups
     * @param jwtToken
     * @return
     * @throws JoseException
     */
    public static JsonWebToken createJwtPrincipal(String username, ArrayList<String> groups, JwtToken jwtToken) throws JoseException {
        // TODO Auto-generated method stub

        String compact = null;
        String type = TYPE_JWT_TOKEN;
        if (jwtToken != null) {
            compact = jwtToken.compact();
            type = (String) jwtToken.getClaims().get(Claims.TOKEN_TYPE);
        }
        String payload = null;
        if (compact != null) {
            String[] parts = JsonUtils.splitTokenString(compact);
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
        }
        jwtclaims.setStringClaim(org.eclipse.microprofile.jwt.Claims.raw_token.name(), compact);
        return new DefaultJsonWebTokenImpl(compact, type, jwtclaims, username);
    }
}
