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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
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
import org.jose4j.lang.JoseException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.impl.utils.ClaimsUtils;

/**
 *
 */
public class DefaultJsonWebTokenImpl implements JsonWebToken, Serializable {
    private static TraceComponent tc = Tr.register(DefaultJsonWebTokenImpl.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     * Names of serializable fields.
     */
    private static final String PRINCIPAL = "principal", JWT = "jwt", TYPE = "type";

    /**
     * Fields to serialize
     */
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[] {
            new ObjectStreamField(PRINCIPAL, String.class),
            new ObjectStreamField(JWT, String.class),
            new ObjectStreamField(TYPE, String.class)
    };

    public static final char SERVICE_NAME_SEPARATOR = ';';
    private transient String principal;
    private transient String jwt;
    private transient String type;
    private JwtClaims claimsSet;

    private ClaimsUtils claimsUtils = new ClaimsUtils();

    public DefaultJsonWebTokenImpl(String name) {
        principal = name;
    }

    public DefaultJsonWebTokenImpl(String jwt, String type, String name) {
        this.jwt = jwt;
        this.type = type;
        //this.claimsSet = claimsSet;
        principal = name;
        handleClaims(jwt);
    }

    @Override
    public final Object clone() {

        return new DefaultJsonWebTokenImpl(this.jwt, this.type, this.principal);

    }

    /**
     * @param jwt
     * @return
     *
     */
    private void handleClaims(String jwt) {
        try {
            if (claimsUtils == null) {
                claimsUtils = new ClaimsUtils();
            }
            this.claimsSet = claimsUtils.getJwtClaims(jwt);
        } catch (JoseException e) {
            // TODO:
        }
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
     * @return DefaultJsonWebTokenImpl string view
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

    /**
     * Deserialize json web token.
     *
     * @param in
     *            The stream from which this object is read.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GetField fields = in.readFields();
        principal = (String) fields.get(PRINCIPAL, null);
        jwt = (String) fields.get(JWT, null);
        type = (String) fields.get(TYPE, null);
        handleClaims(jwt);
    }

    /**
     * Serialize json web token.
     *
     * @param out
     *            The stream to which this object is serialized.
     *
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        PutField fields = out.putFields();
        fields.put(PRINCIPAL, principal);
        fields.put(JWT, jwt);
        fields.put(TYPE, type);
        out.writeFields();
    }

}
