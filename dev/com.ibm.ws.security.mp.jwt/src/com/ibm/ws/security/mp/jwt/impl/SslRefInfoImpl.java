/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.impl;

import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.crypto.SecretKey;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.Constants;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.mp.jwt.SslRefInfo;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.ssl.KeyStoreService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.ssl.SSLSupport;

public class SslRefInfoImpl implements SslRefInfo {
    public static final TraceComponent tc = Tr.register(SslRefInfoImpl.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    SSLSupport sslSupport = null;
    String sslRef = null;
    JSSEHelper jsseHelper = null;
    String sslKeyStoreName = null;
    String sslTrustStoreName = null;
    private String keyAliasName = null;
    AtomicServiceReference<KeyStoreService> keyStoreServiceRef = null;

    public SslRefInfoImpl(SSLSupport sslSupport, AtomicServiceReference<KeyStoreService> keyStoreServiceRef, String sslRef, String keyAliasName) {
        this.sslSupport = sslSupport;
        this.sslRef = sslRef;
        this.keyStoreServiceRef = keyStoreServiceRef;
        this.keyAliasName = keyAliasName;
    }

    @Override
    public String getTrustStoreName() throws MpJwtProcessingException {
        if (sslTrustStoreName == null) {
            init();
        }
        return sslTrustStoreName;
    }

    @Override
    public String getKeyStoreName() throws MpJwtProcessingException {
        if (sslKeyStoreName == null) {
            init();
        }
        return sslKeyStoreName;
    }

    // init when needed
    void init() throws MpJwtProcessingException {
        if (sslSupport != null) {
            Properties sslProps = null;
            this.jsseHelper = sslSupport.getJSSEHelper();
            if (this.jsseHelper != null) {
                try {
                    if (sslRef != null) {
                        sslProps = this.jsseHelper.getProperties(sslRef); // SSLConfig
                    } else {
                        Map<String, Object> connectionInfo = new HashMap<String, Object>();
                        connectionInfo.put(Constants.CONNECTION_INFO_DIRECTION, Constants.DIRECTION_INBOUND);
                        sslProps = this.jsseHelper.getProperties(null, connectionInfo, null, true); // default
                        // SSL
                    }
                } catch (SSLException e) {
                    String msg = Tr.formatMessage(tc, "ERROR_LOADING_SSL_PROPS", new Object[] { e.getLocalizedMessage() });
                    Tr.error(tc, msg);
                    throw new MpJwtProcessingException(msg, e);
                }
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "sslConfig (" + sslRef + ") get: " + sslProps);
                }
                if (sslProps != null) {
                    this.sslKeyStoreName = sslProps.getProperty(com.ibm.websphere.ssl.Constants.SSLPROP_KEY_STORE_NAME);
                    this.sslTrustStoreName = sslProps.getProperty(com.ibm.websphere.ssl.Constants.SSLPROP_TRUST_STORE_NAME);
                }
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "sslTrustStoreName: " + this.sslTrustStoreName);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws MpJwtProcessingException
     */
    @Override
    public HashMap<String, PublicKey> getPublicKeys() throws MpJwtProcessingException {
        if (this.jsseHelper == null) {
            init();
        }
        // TODO due to dynamic changes on keyStore, we have to load the public
        // keys everytime.
        HashMap<String, PublicKey> publicKeys = new HashMap<String, PublicKey>();
        if (this.sslTrustStoreName != null) {
            KeyStoreService keyStoreService = keyStoreServiceRef.getService();
            if (keyStoreService == null) {
                String msg = Tr.formatMessage(tc, "KEYSTORE_SERVICE_NOT_FOUND");
                Tr.error(tc, msg);
                throw new MpJwtProcessingException(msg);
            }
            Collection<String> names = null;
            try {
                names = keyStoreService.getTrustedCertEntriesInKeyStore(sslTrustStoreName);
            } catch (KeyStoreException e) {
                String msg = Tr.formatMessage(tc, "ERROR_LOADING_KEYSTORE_CERTIFICATES", new Object[] { sslTrustStoreName, e.getLocalizedMessage() }); //TODO:
                Tr.error(tc, msg);
                throw new MpJwtProcessingException(msg, e);
            }
            Iterator<String> aliasNames = names.iterator();
            while (aliasNames.hasNext()) {
                String aliasName = aliasNames.next();
                PublicKey publicKey = null;
                try {
                    publicKey = keyStoreService.getCertificateFromKeyStore(sslTrustStoreName, aliasName).getPublicKey();
                } catch (GeneralSecurityException e) {
                    String msg = Tr.formatMessage(tc, "ERROR_LOADING_CERTIFICATE", new Object[] { sslTrustStoreName, e.getLocalizedMessage() }); //TODO:
                    Tr.error(tc, msg);
                    throw new MpJwtProcessingException(msg, e);
                }
                publicKeys.put(aliasName, publicKey);
            }
        }

        return publicKeys;
    }

    /** {@inheritDoc} */
    @FFDCIgnore({ MpJwtProcessingException.class })
    @Override
    public PublicKey getPublicKey() throws MpJwtProcessingException {
        if (this.jsseHelper == null) {
            init();
        }
        if (sslKeyStoreName != null) {
            if (keyAliasName != null && keyAliasName.trim().isEmpty() == false) {
                KeyStoreService keyStoreService = keyStoreServiceRef.getService();
                if (keyStoreService == null) {
                    String msg = Tr.formatMessage(tc, "KEYSTORE_SERVICE_NOT_FOUND");
                    Tr.error(tc, msg);
                    throw new MpJwtProcessingException(msg);
                }
                // TODO: Determine if the first public key should be used if a public key is not found for the key alias.
                try {
                    return keyStoreService.getCertificateFromKeyStore(sslKeyStoreName, keyAliasName).getPublicKey();
                } catch (GeneralSecurityException e) {
                    String msg = Tr.formatMessage(tc, "ERROR_LOADING_CERTIFICATE", new Object[] { sslTrustStoreName, e.getLocalizedMessage() }); //TODO:
                    Tr.error(tc, msg);
                    throw new MpJwtProcessingException(msg, e);
                }
            } else {
                Iterator<Entry<String, PublicKey>> publicKeysIterator = null;
                try {
                    // Get first public key
                    publicKeysIterator = getPublicKeys().entrySet().iterator();
                } catch (MpJwtProcessingException e) {
                    String msg = Tr.formatMessage(tc, "ERROR_LOADING_GETTING_PUBLIC_KEYS", new Object[] { keyAliasName, sslTrustStoreName, e.getLocalizedMessage() });
                    Tr.error(tc, msg);
                    throw e;
                }
                if (publicKeysIterator.hasNext()) {
                    return publicKeysIterator.next().getValue();
                }
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PrivateKey getPrivateKey() throws MpJwtProcessingException {
        //        if (this.jsseHelper == null) {
        //            init();
        //        }
        //        if (sslKeyStoreName != null) {
        //            KeyStoreService keyStoreService = keyStoreServiceRef.getService();
        //            if (keyStoreService == null) {
        //                String msg = Tr.formatMessage(tc, "KEYSTORE_SERVICE_NOT_FOUND");
        //                Tr.error(tc, msg);
        //                throw new MpJwtProcessingException(msg);
        //            }
        //            if (keyAliasName != null && keyAliasName.trim().isEmpty() == false) {
        //                // TODO: Determine if the first private key should be used if a private key is not found for the key alias.
        //                try {
        //                    return keyStoreService.getPrivateKeyFromKeyStore(sslKeyStoreName, keyAliasName, null);
        //                } catch (GeneralSecurityException e) {
        //                    String msg = Tr.formatMessage(tc, "ERROR_LOADING_SPECIFIC_PRIVATE_KEY", new Object[] { keyAliasName, sslKeyStoreName, e.getLocalizedMessage() });
        //                    Tr.error(tc,  msg);
        //                    throw new MpJwtProcessingException(msg, e);
        //                }
        //            } else {
        //                // Get first public key
        //                try {
        //                    return keyStoreService.getPrivateKeyFromKeyStore(sslKeyStoreName);
        //                } catch (GeneralSecurityException e) {
        //                    String msg = Tr.formatMessage(tc, "ERROR_LOADING_PRIVATE_KEY", new Object[] { sslKeyStoreName, e.getLocalizedMessage() });
        //                    Tr.error(tc,  msg);
        //                    throw new MpJwtProcessingException(msg, e);
        //                }
        //            }
        //        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SecretKey getSecretKey() throws MpJwtProcessingException {
        //        if (this.jsseHelper == null) {
        //            init();
        //        }
        //        if (sslKeyStoreName != null) {
        //            if (keyAliasName != null && keyAliasName.trim().isEmpty() == false) {
        //                KeyStoreService keyStoreService = keyStoreServiceRef.getService();
        //                if (keyStoreService == null) {
        //                    throw new MpJwtProcessingException("KEYSTORE_SERVICE_NOT_FOUND", null, new Object[0]);
        //                }
        //                try {
        //                    return keyStoreService.getSecretKeyFromKeyStore(sslKeyStoreName, keyAliasName, null);
        //                } catch (GeneralSecurityException e) {
        //                    throw new MpJwtProcessingException("ERROR_LOADING_SECRET_KEY", e, new Object[] { keyAliasName, sslKeyStoreName, e.getLocalizedMessage() });
        //                }
        //            }
        //        }
        return null;
    }

}
