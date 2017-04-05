/*
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2006, 2007
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * 
 * @(#) 1.3 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/config/CMSKeyStoreUtility.java, WAS.security.crypto, WASX.SERV1, pp0919.25 4/27/07 08:44:48 [5/15/09 18:04:46]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 01/19/06     327178        pbirk      Split the CMS keystore code into a separate class.
 * 10/12/06     387177        alaine    Add check for Classic jvm type on i-series
 * 04/27/07     429153        danmorris Use newCMSLoadParameter when loading instead of newCMSStoreParameter
 */

package com.ibm.ws.ssl.config;

import java.io.File;
import java.security.KeyStore;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.Constants;
import com.ibm.websphere.ssl.JSSEProvider;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.ssl.JSSEProviderFactory;

/**
 * Utility methods specific to CMS keystores.
 * <p>
 * Used to make the CMS code isolated from JDK 1.4.2 environments from class
 * loading standpoint.
 * </p>
 * 
 * @author IBM Corporation
 * @version WAS 6.1
 * @since WAS 6.1
 */
public class CMSKeyStoreUtility {
    private static final TraceComponent tc = Tr.register(CMSKeyStoreUtility.class, "SSL", "com.ibm.ws.ssl.resources.ssl");

    private CMSKeyStoreUtility() {
        // do nothing
    }

    /**
     * Store a CMS keystore.
     * 
     * @param ks
     * @param SSLKeyFile
     * @param SSLKeyPassword
     * @param SSLKeyStoreType
     * @param SSLKeyStoreStash
     * @throws SSLException
     */
    public static void storeCMSKeyStore(KeyStore ks, String SSLKeyFile, String SSLKeyPassword, String SSLKeyStoreType, String SSLKeyStoreStash) throws SSLException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "storeCMSKeyStore");

        KeyStore.LoadStoreParameter loadParm = null;
        File sslKeyFile = new File(SSLKeyFile);
        if (SSLKeyStoreType.equals(Constants.KEYSTORE_TYPE_CMS)) {
            boolean stashFile = Boolean.parseBoolean(SSLKeyStoreStash);
            loadParm = com.ibm.security.cmskeystore.CMSLoadStoreParameterFactory.newCMSStoreParameter(sslKeyFile,
                                                                                                      new KeyStore.PasswordProtection(SSLKeyPassword.toCharArray()), stashFile);
        }

        if (loadParm != null) {
            try {
                ks.store(loadParm);
            } catch (Exception e) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Could not store the keystore.", new Object[] { e });
                throw new SSLException(e.getMessage(), e);
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "storeCMSKeyStore");
    }

    /**
     * Load a CMS key store.
     * 
     * @param kFile
     * @param keyStoreLocation
     * @param SSLKeyPassword
     * @param SSLKeyStoreType
     * @param SSLKeyStoreProvider
     * @param SSLKeyStoreStash
     * @return KeyStore
     * @throws SSLException
     */
    @SuppressWarnings("unused")
    public static KeyStore loadCMSKeyStore(File kFile, String keyStoreLocation, String SSLKeyPassword, String SSLKeyStoreType, String SSLKeyStoreProvider, String SSLKeyStoreStash) throws SSLException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "loadCMSKeyStore");
        KeyStore ks1 = null;

        try {
            KeyStore.LoadStoreParameter loadParm = null;

            if (SSLKeyStoreType.equals(Constants.KEYSTORE_TYPE_CMS)) {
                loadParm = com.ibm.security.cmskeystore.CMSLoadStoreParameterFactory.newCMSLoadParameter(kFile,
                                                                                                         new KeyStore.PasswordProtection(WSKeyStore.decodePassword(SSLKeyPassword).toCharArray()));
            }

            if (loadParm != null) {
                JSSEProvider jsseProvider = JSSEProviderFactory.getInstance(SSLKeyStoreProvider);
                ks1 = jsseProvider.getKeyStoreInstance(SSLKeyStoreType, SSLKeyStoreProvider);
                ks1.load(loadParm);
            }
        } catch (Exception e) {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "Exception loading the CMS keystore.", new Object[] { e });
            throw new SSLException(e.getMessage(), e);
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "loadCMSKeyStore");
        return ks1;
    }
}
