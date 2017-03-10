/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.crypto.ltpakeyutil;

/**
 * Represents an LTPA key pair based on RSA. Encoding format is non-standard. Understood only by LTPA
 * specific classes
 */
public final class LTPAKeyPair {

    private final LTPAPublicKey publicKey;
    private final LTPAPrivateKey privateKey;

    LTPAKeyPair(LTPAPublicKey pubKey, LTPAPrivateKey privKey) {
        publicKey = pubKey;
        privateKey = privKey;
    }

    public LTPAPrivateKey getPrivate() {
        return privateKey;
    }

    public LTPAPublicKey getPublic() {
        return publicKey;
    }
}
