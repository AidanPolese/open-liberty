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

import java.security.MessageDigest;

/**
 * A package local class for performing encryption and decryption of keys
 * based on admin's password
 */
public class KeyEncryptor {

    private static final String MESSAGE_DIGEST_ALGORITHM = "SHA";
    private static final String DES_ECB_CIPHER = "DESede/ECB/PKCS5Padding";

    private final byte[] desKey;

    /**
     * A KeyEncryptor constructor.
     * 
     * @param password The key password
     */
    public KeyEncryptor(byte[] password) throws Exception {
        MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        byte[] digest = md.digest(password);
        desKey = new byte[24];
        System.arraycopy(digest, 0, desKey, 0, digest.length);
        desKey[20] = (byte) 0x00;
        desKey[21] = (byte) 0x00;
        desKey[22] = (byte) 0x00;
        desKey[23] = (byte) 0x00;
    }

    /**
     * Decrypt the key.
     * 
     * @param encryptedKey The encrypted key
     * @return The decrypted key
     */
    public byte[] decrypt(byte[] encryptedKey) throws Exception {
        return LTPACrypto.decrypt(encryptedKey, desKey, DES_ECB_CIPHER);
    }

    public byte[] encrypt(byte[] key) throws Exception {
        return LTPACrypto.encrypt(key, desKey, DES_ECB_CIPHER);
    }
}
