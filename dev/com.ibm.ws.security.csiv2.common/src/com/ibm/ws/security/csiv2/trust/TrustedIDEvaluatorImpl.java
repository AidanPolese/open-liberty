/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.csiv2.trust;

import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.wsspi.security.csiv2.TrustedIDEvaluator;

/**
 *
 */
public class TrustedIDEvaluatorImpl implements TrustedIDEvaluator {

    private final Set<String> trustedIdentities;

    public TrustedIDEvaluatorImpl() {
        trustedIdentities = new HashSet<String>();
    }

    /**
     * @param trustedIdentities
     */
    public TrustedIDEvaluatorImpl(Set<String> trustedIdentities) {
        this.trustedIdentities = trustedIdentities;
    }

    /**
     * @param pipeSeparatedTrustedIdentities The string containing the trusted identities separated by the pipe '|' character.
     */
    public TrustedIDEvaluatorImpl(String pipeSeparatedTrustedIdentities) {
        this(createSetFrom(pipeSeparatedTrustedIdentities));
    }

    private static Set<String> createSetFrom(String pipeSeparatedTrustedIdentities) {
        Set<String> tempTrustedIdentities = new HashSet<String>();
        if (pipeSeparatedTrustedIdentities != null && pipeSeparatedTrustedIdentities.trim().isEmpty() == false) {
            String[] parsedTrustedIdentities = pipeSeparatedTrustedIdentities.split("\\|");

            for (String trustedIdentity : parsedTrustedIdentities) {
                String trimmedTrustedIdentity = trustedIdentity.trim();
                if (trimmedTrustedIdentity.isEmpty() == false) {
                    tempTrustedIdentities.add(trimmedTrustedIdentity);
                }
            }
        }
        return tempTrustedIdentities;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTrusted(String user) {
        return trustedIdentities.contains(user);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTrusted(String user, @Sensitive String password) {
        // TODO: Determine if checking the password matters since the authentication layer already authenticated.
        return isTrusted(user);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTrusted(X509Certificate[] certChain) {
        String issuerDN = certChain[0].getIssuerX500Principal().getName();
        return isTrusted(issuerDN);
    }

}
