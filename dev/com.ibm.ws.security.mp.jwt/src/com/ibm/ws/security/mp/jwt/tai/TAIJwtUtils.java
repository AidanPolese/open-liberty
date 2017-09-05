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
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.JwtConsumer;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.security.mp.jwt.impl.DefaultJsonWebTokenImpl;

public class TAIJwtUtils {

    private static TraceComponent tc = Tr.register(TAIJwtUtils.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);
    public static final String TYPE_JWT_TOKEN = "Json Web Token";

    protected TAIJwtUtils() {
    }

    public JwtToken validateMpJwtToken(@Sensitive String idToken, String jwtConfigId) throws MpJwtProcessingException {

        try {
            return JwtConsumer.create(jwtConfigId).createJwt(idToken);
        } catch (Exception e) {
            String msg = Tr.formatMessage(tc, "AUTH_CODE_FAILED_TO_CREATE_JWT", new Object[] { jwtConfigId, e.getMessage() });
            Tr.error(tc, msg);
            throw new MpJwtProcessingException(msg, e);
        }
    }

    /**
     * @param username
     * @param groups
     * @param jwtToken
     * @return
     * @throws JoseException
     */
    @Sensitive
    public JsonWebToken createJwtPrincipal(String username, ArrayList<String> groups, @Sensitive JwtToken jwtToken) throws JoseException {
        // TODO Auto-generated method stub

        String compact = null;
        String type = TYPE_JWT_TOKEN;
        if (jwtToken != null) {
            compact = jwtToken.compact();
            type = (String) jwtToken.getClaims().get(Claims.TOKEN_TYPE);
        }
        //        String payload = null;
        //        if (compact != null) {
        //            String[] parts = JsonUtils.splitTokenString(compact);
        //            if (parts.length > 0) {
        //                payload = JsonUtils.fromBase64ToJsonString(parts[1]); // payload - claims
        //            }
        //        }
        //        JwtClaims jwtclaims = new JwtClaims();
        //
        //        if (payload != null) {
        //            Map<String, Object> payloadClaims = org.jose4j.json.JsonUtil.parseJson(payload);
        //            Set<Entry<String, Object>> entries = payloadClaims.entrySet();
        //            Iterator<Entry<String, Object>> it = entries.iterator();
        //            while (it.hasNext()) {
        //                Entry<String, Object> entry = it.next();
        //
        //                String key = entry.getKey();
        //                Object value = entry.getValue();
        //                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
        //                    Tr.debug(tc, "Key : " + key + ", Value: " + value);
        //                }
        //                if (key != null && value != null) {
        //                    jwtclaims.setClaim(key, value);
        //                }
        //            }
        //        }
        //        jwtclaims.setStringClaim(org.eclipse.microprofile.jwt.Claims.raw_token.name(), compact);
        //        fixJoseTypes(jwtclaims);
        return new DefaultJsonWebTokenImpl(compact, type, username);
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
