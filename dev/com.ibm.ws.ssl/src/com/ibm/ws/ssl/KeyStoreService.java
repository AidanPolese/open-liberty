/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ssl;

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.crypto.SecretKey;

/**
 * Provides an interface into the contents of the configured keystores
 * for the server.
 * <p>
 * This will allow semi-restricted access to entries within the available
 * keystores for basic certificate management activities.
 */
public interface KeyStoreService {

    /**
     * Returns the location of the keystore.
     *
     * @param keyStoreName The keystore's configuration ID
     * @return the keystore location. {@code null} is not returned.
     * @throws KeyStoreException if the keystore does not exist in the configuration
     */
    String getKeyStoreLocation(String keyStoreName) throws KeyStoreException;

    /**
     * Returns the set of trusted cert entries in the keystore.
     *
     * @param keyStoreName The keystore's configuration ID
     * @return the collection of trusted cert entries. {@code null} is not
     *         returned, but the collection may be empty.
     * @throws KeyStoreException if the keystore does not exist in the configuration
     */
    Collection<String> getTrustedCertEntriesInKeyStore(String keyStoreName) throws KeyStoreException;

    /**
     * Loads the Certificate with the given alias from the specified
     * keystore.
     *
     * @param keyStoreName The keystore's configuration ID
     * @param alias the alias of the certificate to load
     * @return The Certificate for the given alias. {@code null} is not returned.
     * @throws KeyStoreException if the keystore does not exist in the configuration
     * @throws CertificateException if the specified alias does not exist
     */
    Certificate getCertificateFromKeyStore(String keyStoreName, String alias) throws KeyStoreException, CertificateException;

    /**
     * Loads the X509Certificate with the given alias from the specified
     * keystore.
     *
     * @param keyStoreName The keystore's configuration ID
     * @param alias the alias of the certificate to load
     * @return The X509Certificate for the given alias. {@code null} is not returned.
     * @throws KeyStoreException if the keystore does not exist in the configuration
     * @throws CertificateException if the specified alias does not exist or is not an X509 certificate
     */
    X509Certificate getX509CertificateFromKeyStore(String keyStoreName, String alias) throws KeyStoreException, CertificateException;

    /**
     * Loads the private key specified by the alias from the specified
     * keystore.
     * <p>
     * If keyPassword is null, the keystore password will be used.
     *
     * @param keyStoreName The keystore's configuration ID
     * @param alias the alias of the key to load
     * @param keyPassword the keystore password. The password may be encoded.
     * @return The PrivateKey for the given alias. {@code null} is not returned.
     * @throws KeyStoreException if the keystore does not exist in the configuration
     * @throws CertificateException
     */
    PrivateKey getPrivateKeyFromKeyStore(String keyStoreName, String alias, String keyPassword) throws KeyStoreException, CertificateException;

    /**
     * Add the Certificate with the given alias to the specified
     * keystore.
     *
     * @param keyStoreName The keystore's configuration ID
     * @param alias the alias of the certificate to add
     * @return The Certificate for the given alias
     * @throws KeyStoreException if the keystore does not exist in the configuration
     * @throws CertificateException if the specified alias does not exist
     */
    void addCertificateToKeyStore(String keyStoreName, String alias, Certificate certificate) throws KeyStoreException, CertificateException;

    /**
     * @param keyStoreName
     * @return
     * @throws KeyStoreException
     * @throws CertificateException
     */
    PrivateKey getPrivateKeyFromKeyStore(String keyStoreName) throws KeyStoreException, CertificateException;

    /**
     * @param keyStoreName
     * @return
     * @throws KeyStoreException
     * @throws CertificateException
     */
    X509Certificate getX509CertificateFromKeyStore(String keyStoreName) throws KeyStoreException, CertificateException;

    /**
     * Loads the secret key specified by the alias from the specified
     * keystore.
     * <p>
     * If keyPassword is null, the keystore password will be used.
     *
     * @param keyStoreName The keystore's configuration ID
     * @param alias the alias of the key to load
     * @param keyPassword the keystore password. The password may be encoded.
     * @return The SecretKey for the given alias. {@code null} is not returned.
     * @throws KeyStoreException if the keystore does not exist in the configuration
     * @throws CertificateException
     */
    SecretKey getSecretKeyFromKeyStore(String keyStoreName, String alias, String keyPassword) throws KeyStoreException, CertificateException;

}
