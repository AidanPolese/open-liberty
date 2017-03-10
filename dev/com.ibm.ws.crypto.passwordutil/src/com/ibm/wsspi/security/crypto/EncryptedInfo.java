/*
 * COMPONENT_NAME:  WAS.orbext
 * @(#) 1.1 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/security/crypto/EncryptedInfo.java, WAS.security.crypto, WASX.SERV1, pp0919.25 5/12/05 16:30:34 [5/15/09 18:04:39]
 *
 * ORIGINS: 27
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION: Interface that defines how a customer implements password encryption.
 *
 *             
 * Change History:
 * 
 *  Date       Programmer       Defect        Description
 *  --------   ---------        ------        -------------------------------------
 *  05/12/05   pbirk            fLIDB4135.2   Initial drop of the interface.
 */

package com.ibm.wsspi.security.crypto;

/**
 * Return code information for password utilities, deciphering or enciphering.
 */
public class EncryptedInfo {
    private final byte[] bytes;
    private final String alias;

    /**
     * This constructor takes the encrypted bytes and a keyAlias as parameters.
     * This is for passing to/from the WebSphere Application Server runtime so the
     * runtime can associate the bytes with a specific key used to encrypt the
     * bytes.
     * 
     * @param encryptedBytes
     * @param keyAlias
     */

    public EncryptedInfo(byte[] encryptedBytes, String keyAlias) {
        this.bytes = encryptedBytes == null ? null : encryptedBytes.clone();
        this.alias = keyAlias;
    }

    /**
     * This returns the encrypted bytes.
     * 
     * @return byte[]
     */
    public byte[] getEncryptedBytes() {
        return bytes == null ? null : bytes.clone();
    }

    /**
     * This returns the key alias. This key alias is a logical string associated
     * with the encrypted password in the model. The format is
     * {custom:keyAlias}encrypted_password. Typically just the key alias is put
     * here, but algorithm information could also be returned.
     * 
     * @return String
     */
    public String getKeyAlias() {
        return this.alias;
    }

}
