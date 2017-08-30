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
package com.ibm.ws.security.mp.jwt.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 *
 */
public class DefaultJsonWebTokenImpl implements JsonWebToken {

    public static final char SERVICE_NAME_SEPARATOR = ';';
    String principal;
    private String jwt;
    private String type;
    private JwtClaims claimsSet;

    public DefaultJsonWebTokenImpl(String name) {
        principal = name;
    }

    public DefaultJsonWebTokenImpl(String jwt, String type, JwtClaims claimsSet, String name) {
        this.jwt = jwt;
        this.type = type;
        this.claimsSet = claimsSet;
        principal = name;
    }

    @Override
    public <T> Optional<T> claim(String claimName) {
        T claim = (T) getClaim(claimName);
        return Optional.ofNullable(claim);
    }
    //
    //        /** {@inheritDoc} */
    //        @Override
    //        public <T> T getClaim(String arg0) {
    //            // TODO Auto-generated method stub
    //            return null;
    //        }

    @Override
    @FFDCIgnore({ IllegalArgumentException.class })
    public Object getClaim(String claimName) {
        Claims claimType = Claims.UNKNOWN;
        Object claim = null;
        try {
            claimType = Claims.valueOf(claimName);
        } catch (IllegalArgumentException e) {

        }
        // Handle the jose4j NumericDate types and
        switch (claimType) {
        case exp:
        case iat:
        case auth_time:
        case nbf:
        case updated_at:
            try {
                claim = claimsSet.getClaimValue(claimType.name(), Long.class);
                if (claim == null) {
                    claim = new Long(0);
                }
            } catch (MalformedClaimException e) {
            }
            break;
        case groups:
            claim = getGroups();
            break;
        case aud:
            claim = getAudience();
            break;
        case UNKNOWN:
            claim = claimsSet.getClaimValue(claimName);
            break;
        default:
            claim = claimsSet.getClaimValue(claimType.name());
        }
        return claim;
    }

    //    @Override
    //    public boolean implies(Subject subject) {
    //        return false;
    //    }

    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * TODO: showAll is ignored and currently assumed true
     *
     * @param showAll
     *            - should all claims associated with the JWT be displayed or should only those defined in the
     *            JsonWebToken interface be displayed.
     * @return JWTCallerPrincipal string view
     */
    //@Override
    public String toString(boolean showAll) {
        String toString = "DefaultJsonWebTokenImpl{" +
                "id='" + getTokenID() + '\'' +
                ", name='" + getName() + '\'' +
                ", expiration=" + getExpirationTime() +
                ", notBefore=" + getClaim(Claims.nbf.name()) +
                ", issuedAt=" + getIssuedAtTime() +
                ", issuer='" + getIssuer() + '\'' +
                ", audience=" + getAudience() +
                ", subject='" + getSubject() + '\'' +
                ", type='" + type + '\'' +
                ", issuedFor='" + getClaim("azp") + '\'' +
                ", raw_token='" + getRawToken() + '\'' +
                //                ", authTime=" + getClaim("auth_time") +
                //                ", givenName='" + getClaim("given_name") + '\'' +
                //                ", familyName='" + getClaim("family_name") + '\'' +
                //                ", middleName='" + getClaim("middle_name") + '\'' +
                //                ", nickName='" + getClaim("nickname") + '\'' +
                //                ", preferredUsername='" + getClaim("preferred_username") + '\'' +
                //                ", email='" + getClaim("email") + '\'' +
                //                ", emailVerified=" + getClaim(Claims.email_verified.name()) +
                //                ", allowedOrigins=" + getClaim("allowedOrigins") +
                //                ", updatedAt=" + getClaim("updated_at") +
                ", acr='" + getClaim("acr") + '\'';
        StringBuilder tmp = new StringBuilder(toString);
        tmp.append(", groups=[");
        for (String group : getGroups()) {
            tmp.append(group);
            tmp.append(',');
        }
        tmp.setLength(tmp.length() - 1);
        tmp.append("]}");
        return tmp.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getClaimNames() {
        return new HashSet<String>(claimsSet.getClaimNames());
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return principal;
    }

    @Override
    public Set<String> getAudience() {
        Set<String> audSet = new HashSet<String>();
        try {
            List<String> audList = claimsSet.getStringListClaimValue("aud");
            if (audList != null) {
                audSet.addAll(audList);
            }
        } catch (MalformedClaimException e) {
            try {
                String aud = claimsSet.getStringClaimValue("aud");
                audSet.add(aud);
            } catch (MalformedClaimException e1) {
            }
        }
        return audSet;
    }

    @Override
    public Set<String> getGroups() {
        HashSet<String> groups = new HashSet<String>();
        try {
            List<String> globalGroups = claimsSet.getStringListClaimValue("groups");
            if (globalGroups != null) {
                groups.addAll(globalGroups);
            }
        } catch (MalformedClaimException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * Convert the types jose4j uses for address, sub_jwk, and jwk
     */
    private void fixJoseTypes() {
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
            replaceMap("address");
        }
        if (claimsSet.hasClaim("jwk")) {
            replaceMap("jwk");
        }
        if (claimsSet.hasClaim("sub_jwk")) {
            replaceMap("sub_jwk");
        }
    }

    /**
     * Replace the jose4j Map<String,Object> with a JsonObject
     *
     * @param name
     *            - claim name
     */
    private void replaceMap(String name) {
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

}
