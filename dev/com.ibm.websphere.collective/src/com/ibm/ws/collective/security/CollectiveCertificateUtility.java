/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.collective.security;

import java.security.KeyStoreException;
import java.security.cert.CertificateException;

/**
 * Collection of utilities for generating the appropriate certificates and
 * keystores when responding to a collective join or replicate.
 */
public interface CollectiveCertificateUtility {

    /**
     * Generate a keystore that contains the serverIdentity certificate
     * for the specified collective controller.
     * 
     * @param hostName the host name
     * @param userDir the URL encoded user dir
     * @param serverName the server name
     * @param validity the number of days the generated certificate will be valid for
     * @param password the keystore password
     * @return the bytes representing the serverIdentity.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getControllerServerIdentityJKSBytes(String hostName, String userDir,
                                               String serverName, int validity,
                                               String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains the appropriate trusted certificate
     * entries for a collective controller's outbound collective connection.
     * 
     * @param password the keystore password
     * @return the bytes representing the collectiveTrust.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getControllerCollectiveTrustJKSBytes(String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains the HTTPS certificate for a
     * collective controller.
     * 
     * @param password the keystore password
     * @param subjectDN the subject to be used for the certificate
     * @param validity the number of days the generated certificate will be valid for
     * @return the bytes representing the key.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getControllerKeyJKSBytes(String subjectDN, int validity, String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains the appropriate trusted certificate
     * entries for a collective controller's HTTPS port.
     * 
     * @param password the keystore password
     * @return the bytes representing the trust.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getControllerTrustJKSBytes(String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Gets the bytes for the collective's root keystore.
     * 
     * @return the bytes representing the rootKeys.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getRootKeystoreJKSBytes()
                    throws KeyStoreException;

    /**
     * Generate a keystore that contains the serverIdentity certificate
     * for the specified collective member.
     * 
     * @param hostName the host name
     * @param userDir the URL encoded user dir
     * @param serverName the server name
     * @param validity the number of days the generated certificate will be valid for
     * @param password the keystore password
     * @return the bytes representing the serverIdentity.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberServerIdentityJKSBytes(String hostName, String userDir,
                                           String serverName, int validity,
                                           String password) throws CertificateException, KeyStoreException;

    /**
     * Generate a pfx file that contains the serverIdentity certificate
     * for the specified collective member.
     * 
     * @param hostName the host name
     * @param userDir the URL encoded user dir
     * @param serverName the server name
     * @param validity the number of days the generated certificate will be valid for
     * @param password the pfx file
     * @return the bytes representing the serverIdentity.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberServerIdentityPFXBytes(String hostName, String userDir,
                                           String serverName, int validity,
                                           String password) throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains the appropriate trusted certificate
     * entries for a collective member's outbound collective connection.
     * 
     * @param password the keystore password
     * @return the bytes representing the collectiveTrust.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberCollectiveTrustJKSBytes(String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a pfx file that contains the appropriate trusted certificate
     * entries for a collective member's outbound collective connection.
     * 
     * @param password the pfx file password
     * @return the bytes representing the collectiveTrust.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberCollectiveTrustPFXBytes(String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains the HTTPS certificate for a
     * collective member.
     * 
     * @param password the keystore password
     * @param subjectDN the subject to be used for the certificate
     * @param validity the number of days the generated certificate will be valid for
     * @return the bytes representing the key.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberKeyJKSBytes(String subjectDN, int validity, String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a pfx file that contains the HTTPS certificate for a
     * collective member.
     * 
     * @param password the pfx file password
     * @param subjectDN the subject to be used for the certificate
     * @param validity the number of days the generated certificate will be valid for
     * @return the bytes representing the key.pfx
     * @throws CertificateException if there was a problem creating the pfx file
     */
    byte[] getMemberKeyPFXBytes(String subjectDN, int validity, String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains a client certificate
     * signed by memberRoot and the memberRoot public certificate so
     * keystore can double as a trust store.
     * 
     * @param validity the number of days the generated certificate will be valid for
     * @param password the keystore password
     * @param addMemberRoot if true memberRoot signer is added to the generated keystore
     * @return the bytes representing the serverIdentity.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getClientKeysJKSBytes(String subjectDN, int validity,
                                 String password, boolean addMemberRoot)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a keystore that contains the appropriate trusted certificate
     * entries for a collective member's HTTPS port.
     * 
     * @param password the keystore password
     * @return the bytes representing the trust.jks
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberTrustJKSBytes(String password)
                    throws CertificateException, KeyStoreException;

    /**
     * Generate a pfx file that contains the appropriate trusted certificate
     * entries for a collective member's HTTPS port.
     * 
     * @param password the pfx file password
     * @return the bytes representing the trust.pfx
     * @throws CertificateException if there was a problem creating the keystore
     */
    byte[] getMemberTrustPFXBytes(String password)
                    throws CertificateException, KeyStoreException;

}
