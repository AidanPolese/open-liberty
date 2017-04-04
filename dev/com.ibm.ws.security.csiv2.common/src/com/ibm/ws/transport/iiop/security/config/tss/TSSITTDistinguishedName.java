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
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.tss;

import javax.security.auth.Subject;

import org.omg.CSI.ITTDistinguishedName;
import org.omg.CSI.IdentityToken;
import org.omg.IOP.Codec;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.authentication.AuthenticationException;
import com.ibm.ws.security.csiv2.Authenticator;
import com.ibm.ws.security.csiv2.SecurityMinorCodes;
import com.ibm.ws.transport.iiop.security.SASException;
import com.ibm.ws.transport.iiop.security.SASInvalidEvidenceException;
import com.ibm.ws.transport.iiop.security.util.Util;

/**
 * @version $Rev: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public class TSSITTDistinguishedName extends TSSSASIdentityToken {

    public static final String OID = "";
    private final String realmName;
    private final String domainName;
    private transient Authenticator authenticator;

    public TSSITTDistinguishedName(String realmName, String domainName) {
        this.realmName = realmName;
        this.domainName = domainName;
        this.authenticator = null;
    }

    /**
     * @param authenticator
     * @param realmName
     * @param domainName
     */
    public TSSITTDistinguishedName(Authenticator authenticator) {
        this.authenticator = authenticator;
        this.realmName = null;
        this.domainName = null;
    }

    @Override
    public short getType() {
        return ITTDistinguishedName.value;
    }

    @Override
    public String getOID() {
        return OID;
    }

    @FFDCIgnore(AuthenticationException.class)
    @Override
    public Subject check(IdentityToken identityToken, Codec codec) throws SASException {
        Subject identityAssertionSubject = null;

        try {
            String dn = getDistinguishedName(identityToken, codec);
            identityAssertionSubject = authenticator.authenticate(dn);
        } catch (AuthenticationException e) {
            throw new SASInvalidEvidenceException(e.getMessage(), SecurityMinorCodes.AUTHENTICATION_FAILED);
        }
        return identityAssertionSubject;
    }

    private String getDistinguishedName(IdentityToken identityToken, Codec codec) throws SASException {
        byte[] encodedDN = identityToken.dn();
        return Util.decodeDN(codec, encodedDN);
    }

    @Override
    @Trivial
    public void toString(String spaces, StringBuilder buf) {
        String moreSpaces = spaces + "  ";
        buf.append(spaces).append("TSSITTDistinguishedName: [\n");
        buf.append(moreSpaces).append("domain: ").append(domainName).append("\n");
        buf.append(moreSpaces).append("realm: ").append(realmName).append("\n");
        buf.append(spaces).append("]\n");
    }

}
