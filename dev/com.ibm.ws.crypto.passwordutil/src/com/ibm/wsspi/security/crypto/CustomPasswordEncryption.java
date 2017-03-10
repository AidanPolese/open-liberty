/*
 * COMPONENT_NAME:  WAS.orbext
 * @(#) 1.1 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/security/crypto/CustomPasswordEncryption.java, WAS.security.crypto, WASX.SERV1, pp0919.25 5/12/05 16:29:32 [5/15/09 18:04:38]
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

import java.util.Map;

/**
 * The interface for encrypting or decrypting the sensitive data.
 * @ibm-spi
 */
public interface CustomPasswordEncryption {

    /**
     * The encrypt operation takes a UTF-8 encoded String in the form of a byte[].
     * The byte[] is generated from String.getBytes("UTF-8"). An encrypted byte[]
     * is returned from the implementation in the EncryptedInfo object.
     * Additionally, a logically key alias is returned in EncryptedInfo so which
     * is passed back into the decrypt method to determine which key was used to
     * encrypt this password. The WebSphere Application Server runtime has no
     * knowledge of the algorithm or key used to encrypt the data.
     * 
     * @param decrypted_bytes
     * @return com.ibm.wsspi.security.crypto.EncryptedInfo
     * @throws com.ibm.wsspi.security.crypto.PasswordEncryptException
     **/
    EncryptedInfo encrypt(byte[] decrypted_bytes) throws PasswordEncryptException;

    /**
     * The decrypt operation takes the EncryptedInfo object containing a byte[]
     * and the logical key alias and converts it to the decrypted byte[]. The
     * WebSphere Application Server runtime will convert the byte[] to a String
     * using new String (byte[], "UTF-8");
     * 
     * @param info
     * @return byte[]
     * @throws com.ibm.wsspi.security.crypto.PasswordDecryptException
     **/
    byte[] decrypt(EncryptedInfo info) throws PasswordDecryptException;

    /**
     * This is reserved for future use and is currently not called by the
     * WebSphere Application Server runtime.
     * 
     * @param initialization_data
     **/
    void initialize(Map initialization_data);
}
