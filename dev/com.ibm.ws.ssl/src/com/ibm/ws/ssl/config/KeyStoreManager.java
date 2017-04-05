/*
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2005, 2009
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 *  @(#) 1.77 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/config/KeyStoreManager.java, WAS.security.crypto, WASX.SERV1, pp0919.25 3/17/09 13:08:27 [5/15/09 18:04:34]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 * 09/20/05     LIDB3557-1.7  pbirk      3557 Code Drop #2
 * 09/30/05     LIDB3919-23.1 aruna      HW crypto changes
 * 10/12/05     311587        riddlemo   Correcting version comments.
 * 10/27/05     313879        aruna      use hw crypto card configuration file to initialize hw provider
 * 11/07/05     318812        pbirk      Moved JDK 5 APIs out of here into a CommandTask that needed it.
 * 11/16/05     324361        pbirk      KeyStores not filtering by scope in runtime.
 * 11/16/05     324405        pbirk      The getOutputStream throws unnecessary exception when keystore is empty.
 * 12/13/05     329678        pbirk      Resolve some migration issues.
 * 12/16/05     LIDB3187-63.1 pbirk      SSSL toleration
 * 01/20/06     324958.4      aruna      changes to support pure hw crypto acceleration
 * 01/26/06     334186.1      pbirk      In exchangeSigners, load keystores directly.
 * 01/31/06     342592        pbirk      Changes to handle SSSL correctly.
 * 02/03/06     344070        pbirk      File.exists should be in a dopriv
 * 02/20/06     348784        pbirk      WASKeyRing must use password="password";
 * 03/03/06     352305        pbirk      Resolve NPE due to keyring fixup.
 * 03/01/06     349527        riddlemo   KEYSTORE_TYPE_RACFCRYPTO and KEYSTORE_TYPE_JAVACRYPTO should always be read only and are not file based
 * 03/07/06     352868        pbirk      Catch NoClassDefFound for digest method on PAC.
 * 03/23/06     347474        alaine     make read only if RACF key store type
 * 03/27/06     353834        alaine     make read only and not file based if RACF key store type
 * 04/05/06     359647        alaine     change expand to handle ${CONFIG_ROOT} and ${WORKSPACE_ROOT}
 * 04/07/06     361207        pbirk      Expand ${USER_INSTALL_ROOT} on pure client for migration.
 * 04/07/06     361167        alaine     Fix so that client side can expand ${CONFIG_ROOT} and ${WORKSPACE_ROOT}
 * 04/12/06     346507        leou       Do not attempt to create keystore or self-signed cert in pluggable clients
 * 04/13/06     362663        elisa      Changed property name to SSLPROP_KEY_STORE_NAME that is set when creating keystore 
 * 04/14/06     362458        pbirk      Configure fileBased correctly based on keystore type.
 * 04/15/06     359841        pbirk      Expand CONFIG_ROOT using other options beside was.repository.root.
 * 04/18/06     363330        alaine     Change call to getKeyStore
 * 04/25/06     364383        pbirk      Decode system properties.
 * 08/03/06     379561        alaine     Added code to make getKeyStore load CMS key stores correctly.
 * 11/03/06     400749        pbirk      Remove the java keystore from WSKeyStore during signerExchange from addNode.
 * 11/08/06     387997        alaine     Change the lifespan on the certificate.
 * 11/17/06     LIDB4119-33   paulben    RCS changes
 * 12/06/06     409252        danmorris  add cast to (Map.Entry) in clearJavaKeyStoresFromKeyStoreMap
 * 02/13/07     419375        paulben    Must explicitly decode passwords with RCS
 * 02/20/07     LIDB2112-24.3 alaine     Add description parameter to keyStoreInfo create
 * 02/28/07     423300        alaine     add check for was.install.root in expand
 * 03/26/07     LIDB4134-62.2 danmorris  Modify exchangeSigners to exchange the root cert in the cert chain
 * 04/02/07     428875        danmorris  Handle CMS keystores in checkIfClientKeyStoreAndTrustStoreExistsAndCreateIfNot
 * 04/16/07     432337        danmorris  Do not force non-z/os client keystores to be writable
 * 05/03/07     PK43266       lisarich   Expand ${hostname} 
 * 05/29/07     436619        pbirk      Changes that help SSL acceleration work.
 * 06/28/07     LIDB2112-22.1 danmorris  Update self-signed certificate creation to use chained certificates
 * 07/11/07     LI4119.80     paulben    RCS var expansion
 * 08/07/07     457211        danmorris  Modified call to chainedCertificateCreate to match root key store changes
 * 08/30/07     459104        pbirk      Support for AdminAgent configuration.
 * 09/04/07     464499        danmorris  Remove issued keystore implementation
 * 09/14/07     464970        danmorris  Cap the size of expandMap at 50 to avoid mem leaks
 * 09/28/07     LIDB2112-26.1 danmorris  Writable SAF keyring support
 * 10/01/07     LIDB4134-91.09 pbirk     FileTransferServlet security for Job Manager.
 * 10/18/07     475457        pbirk      Make the clearJavaKeyStoreCache public to be called from WSX509TrustManager
 * 10/28/07     LIDB4194-79.1 pbirk      Include JobManager process type.
 * 11/09/07     481831        alaine     add key store scope to getKeyStore()
 * 11/12/07     477704        mcasile    New FFDC API
 * 02/05/08     495916        pbirk      Make sure the default keystore name considers the type of keystore for dmgr.
 * 05/08/08     519012        danmorris  Publicize methods added in 459104
 * 06/01/08     523668        danmorris  Add yet another getKeyStore method to get an RCS object (will load based on uuid context)
 * 06/06/08     527623.2      mcthomps   Clean up from static analysis
 * 06/09/08     521668.1      danmorris  Change getKeyStore to use SecurityConfig.getSCO to handle profile context lookup's
 * 06/24/08     521245        danmorris  Add additional checks for RSA KeyStore types
 * 07/28/08     539053        danmorris  Get passwords types with getUnexpandedString
 * 08/19/08     544007        alaine     add a method for migration to clear out the keystore cache
 * 09/25/08     552038        mcthomps   getKeyStore(String) handles AdminAgent subsystem case
 * 10/22/08     557444        paulben    Back out 552038. It turns out that RSA is dependent on this class NOT being thread context aware
 * 02/18/09     pk69062       alaine     Add getJavaKeyStore for web services team
 * 01/08/08     PK77884       ttorres    Use system properties for keystore and truststore if available instead of default values
 */

package com.ibm.ws.ssl.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.Constants;
import com.ibm.websphere.ssl.SSLConfig;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.ssl.core.WSPKCSInKeyStore;
import com.ibm.ws.ssl.core.WSPKCSInKeyStoreList;

/**
 * KeyStore configuration manager
 * <p>
 * This class handles the configuring, loading/reloading, verifying, etc. of
 * KeyStore objects in the runtime.
 * </p>
 * 
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
public class KeyStoreManager {
    protected static final TraceComponent tc = Tr.register(KeyStoreManager.class, "SSL", "com.ibm.ws.ssl.resources.ssl");

    private static class Singleton {
        static final KeyStoreManager INSTANCE = new KeyStoreManager();
    }

    private final Map<String, WSKeyStore> keyStoreMap = new HashMap<String, WSKeyStore>();
    private static WSPKCSInKeyStoreList pkcsStoreList = new WSPKCSInKeyStoreList();

    /** HEX character list */
    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Do nothing constructor, used to enforce singleton model.
     */
    private KeyStoreManager() {}

    /**
     * Access the singleton instance of the key store manager.
     * 
     * @return KeyStoreManager
     */
    public static KeyStoreManager getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * Load the provided list of keystores from the configuration.
     * 
     * @param config
     */
    public void loadKeyStores(Map<String, WSKeyStore> config) {
        // now process each keystore in the provided config
        for (Entry<String, WSKeyStore> current : config.entrySet()) {
            try {
                String name = current.getKey();
                WSKeyStore keystore = current.getValue();
                addKeyStoreToMap(name, keystore);
            } catch (Exception e) {
                FFDCFilter.processException(e, getClass().getName(), "loadKeyStores", new Object[] { this, config });
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "Error loading keystore; " + current.getKey() + " " + e);
                }
            }
        }
    }

    /***
     * Adds the keyStore to the keyStoreMap.
     * 
     * @param keyStoreName
     * @param ks
     * @throws Exception
     ***/
    public void addKeyStoreToMap(String keyStoreName, WSKeyStore ks) throws Exception {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "addKeyStoreToMap: " + keyStoreName + ", ks=" + ks);

        keyStoreMap.put(keyStoreName, ks);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "addKeyStoreToMap");
    }

    /***
     * Iterates through trusted certificate entries to ensure the signer does not
     * already exist.
     * 
     * @param signer
     * @param trustStore
     * @return boolean
     ***/
    public boolean checkIfSignerAlreadyExistsInTrustStore(X509Certificate signer, KeyStore trustStore) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "checkIfSignerAlreadyExistsInTrustStore");

        try {
            String signerMD5Digest = generateDigest("MD5", signer);
            if (signerMD5Digest == null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                    Tr.exit(tc, "checkIfSignerAlreadyExistsInTrustStore -> false (could not generate digest)");
                return false;
            }

            Enumeration<String> aliases = trustStore.aliases();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();

                if (trustStore.containsAlias(alias)) {
                    X509Certificate cert = (X509Certificate) trustStore.getCertificate(alias);

                    String certMD5Digest = generateDigest("MD5", cert);

                    if (signerMD5Digest.equals(certMD5Digest)) {
                        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                            Tr.exit(tc, "checkIfSignerAlreadyExistsInTrustStore -> true (digest matches)");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            FFDCFilter.processException(e, getClass().getName(), "checkIfSignerAlreadyExistsInTrustStore", this);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "Exception checking if signer already exists; " + e);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "checkIfSignerAlreadyExistsInTrustStore -> false (no digest matches)");
        return false;
    }

    /***
     * Returns the WSKeyStore object given the keyStoreName.
     * 
     * @param keyStoreName
     * @return WSKeyStore
     ***/
    public WSKeyStore getKeyStore(String keyStoreName) {
        WSKeyStore ks = keyStoreMap.get(keyStoreName);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            if (ks != null) {
                Tr.debug(tc, "Returning a keyStore for name: " + keyStoreName);
            } else {
                Tr.debug(tc, "Cannot find a keyStore for name: " + keyStoreName);
            }
        }

        return ks;
    }

    /***
     * Returns a String[] of all WSKeyStore aliases for this process.
     * 
     * @return String[]
     ***/
    public String[] getKeyStoreAliases() {
        Set<String> set = keyStoreMap.keySet();
        return set.toArray(new String[set.size()]);
    }

    /**
     * Fetch the keystore based on the input parameters.
     * 
     * @param name
     * @param type
     *            the type of keystore
     * @param provider
     *            provider associated with the key store
     * @param fileName
     *            location of the key store file
     * @param password
     *            used to access the key store
     * @param create
     * @param sslConfig
     * @return resulting key store
     * @throws Exception
     */
    public KeyStore getKeyStore(String name, String type, String provider, String fileName, String password, boolean create, SSLConfig sslConfig) throws Exception {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getKeyStore", new Object[] { name, type, provider, fileName, Boolean.valueOf(create), SSLConfigManager.mask(password) });

        if (name != null && !create) {
            WSKeyStore keystore = keyStoreMap.get(name);

            if (keystore != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                    Tr.exit(tc, "getKeyStore (from WSKeyStore)");
                return keystore.getKeyStore(false, false);
            }
        }

        KeyStore keyStore = null;
        InputStream inputStream = null;
        boolean not_finished = true;
        int retry_count = 0;
        boolean fileBased = true;
        List<String> keyStoreTypes = new ArrayList<String>();

        // Loop until flag indicates a key store was found or failure occured.
        while (not_finished) {
            boolean isCMS = Constants.KEYSTORE_TYPE_CMS.equals(type);
            // Get a base instance of the keystore based on the type and or the
            // provider.
            if (Constants.KEYSTORE_TYPE_JCERACFKS.equals(type) || Constants.KEYSTORE_TYPE_JCECCARACFKS.equals(type) || Constants.KEYSTORE_TYPE_JCEHYBRIDRACFKS.equals(type))
                fileBased = false;
            else
                fileBased = true;

            // Get a base instance of the keystore based on the type and or the
            // provider.
            char[] passphrase = null;
            if (!isCMS) {
                keyStore = KeyStore.getInstance(type);
                // Convert the key store password into a char array.
                if (password != null) {
                    passphrase = WSKeyStore.decodePassword(password).toCharArray();
                }
            }

            // Open the file specified by the input parms as the keystore file.
            try {
                if (Constants.KEYSTORE_TYPE_JAVACRYPTO.equals(type)) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Creating PKCS11 keystore.");

                    WSPKCSInKeyStore pKS = pkcsStoreList.insert(type, fileName, password, false, provider);

                    if (pKS != null) {
                        keyStore = pKS.getKS();
                        not_finished = false;
                    }
                } else if (null == fileName) {
                    if (isCMS) {
                        keyStore = CMSKeyStoreUtility.loadCMSKeyStore(null, null, password, type, provider, "true");
                        not_finished = false;
                    } else {
                        keyStore.load(null, passphrase);
                        not_finished = false;
                    }
                } else {
                    File f = new File(fileName);

                    FileExistsAction action = new FileExistsAction(f);
                    Boolean fileExists = AccessController.doPrivileged(action);

                    if (!fileExists.booleanValue() && fileBased) {
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "getKeyStore created new KeyStore: " + fileName);
                        }
                        // CMS key stores are load and store differently so let's check the
                        // key store type.
                        if (isCMS) {
                            keyStore = CMSKeyStoreUtility.loadCMSKeyStore(null, fileName, password, type, provider, "true");
                            not_finished = false;
                        } else {
                            keyStore.load(null, passphrase);
                            not_finished = false;
                        }
                    } else {
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "getKeyStore created a new inputStream: " + fileName);
                        }
                        if (isCMS) {
                            keyStore = CMSKeyStoreUtility.loadCMSKeyStore(f, fileName, password, type, provider, "true");
                            not_finished = false;
                        } else {
                            // Access the keystore input stream from a File or URL
                            inputStream = getInputStream(fileName, create);
                            keyStore.load(inputStream, passphrase);
                            not_finished = false;
                        }
                    }
                }
            } catch (IOException e) {
                // Check for well known error conditions.
                if (e.getMessage().equalsIgnoreCase("Invalid keystore format") || e.getMessage().indexOf("DerInputStream.getLength()") != -1) {
                    if (retry_count == 0) {
                        String alias = "unknown";
                        if (sslConfig != null) {
                            alias = sslConfig.getProperty(Constants.SSLPROP_ALIAS);
                        }

                        Tr.warning(tc, "ssl.keystore.type.invalid.CWPKI0018W", new Object[] { type, alias });
                        keyStoreTypes = new ArrayList<String>(Security.getAlgorithms("KeyStore"));
                    }

                    // Limit the number of retries.
                    if (retry_count >= keyStoreTypes.size()) {
                        throw e;
                    }

                    // Adjust the type for another try.
                    // We'll go through all available types.
                    type = keyStoreTypes.get(retry_count++);
                    if (type.equals("PKCS11") || type.equals("IBMCMSKS")) {
                        type = keyStoreTypes.get(retry_count++);
                    }

                } else {
                    // Unknown error condition.
                    throw e;
                }
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        } // end while

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getKeyStore (from SSLConfig properties)");
        return keyStore;
    }

    /**
     * Open the provided filename as a keystore, creating if it doesn't exist and
     * the input create flag is true.
     * 
     * @param fileName
     * @param create
     * @return InputStream
     * @throws MalformedURLException
     * @throws IOException
     */
    public InputStream getInputStream(String fileName, boolean create) throws MalformedURLException, IOException {
        try {
            GetKeyStoreInputStreamAction action = new GetKeyStoreInputStreamAction(fileName, create);
            return AccessController.doPrivileged(action);
        } catch (PrivilegedActionException e) {
            Exception ex = e.getException();
            FFDCFilter.processException(e, getClass().getName(), "getInputStream", new Object[] { fileName, Boolean.valueOf(create), this });
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "Exception opening keystore; " + ex);

            if (ex instanceof MalformedURLException)
                throw (MalformedURLException) ex;
            else if (ex instanceof IOException)
                throw (IOException) ex;

            throw new IOException(ex.getMessage());
        }
    }

    /**
     * This class is used to enable the code to read keystores.
     */
    private static class GetKeyStoreInputStreamAction implements PrivilegedExceptionAction<InputStream> {
        private String file = null;
        private boolean createStream = false;

        /**
         * Constructor.
         * 
         * @param fileName
         * @param create
         */
        public GetKeyStoreInputStreamAction(String fileName, boolean create) {
            file = fileName;
            createStream = create;
        }

        @Override
        public InputStream run() throws MalformedURLException, IOException {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.entry(tc, "GetKeyStoreInputStreamAction.run: " + file);

            InputStream fis = null;
            URL urlFile = null;

            // Check if the filename exists as a File.
            File kfile = new File(file);

            if (createStream && !kfile.exists()) {
                if (!kfile.createNewFile()) {
                    throw new IOException("Unable to create file");
                }
                urlFile = kfile.toURI().toURL();
            } else {
                if (kfile.exists() && kfile.length() == 0) {
                    throw new IOException("Keystore file exists, but is empty: " + file);
                } else if (!kfile.exists()) {
                    // kfile does not exist as a File, treat as URL
                    urlFile = new URL(file);
                } else {
                    // kfile exists as a File
                    urlFile = kfile.toURI().toURL();
                }
            }

            // Finally open the file.
            fis = urlFile.openStream();
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "GetKeyStoreInputStreamAction.run");
            return fis;
        }
    }

    /**
     * Open the provided filename as an outputstream.
     * 
     * @param fileName
     * @return OutputStream
     * @throws MalformedURLException
     * @throws IOException
     */
    public OutputStream getOutputStream(String fileName) throws MalformedURLException, IOException {
        try {
            GetKeyStoreOutputStreamAction action = new GetKeyStoreOutputStreamAction(fileName);
            return AccessController.doPrivileged(action);
        } catch (PrivilegedActionException e) {
            Exception ex = e.getException();
            FFDCFilter.processException(e, getClass().getName(), "getOutputStream", new Object[] { fileName, this });
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "Exception opening keystore; " + ex);

            if (ex instanceof MalformedURLException)
                throw (MalformedURLException) ex;
            else if (ex instanceof IOException)
                throw (IOException) ex;

            throw new IOException(ex.getMessage());
        }
    }

    /**
     * This class is used to enable the code to read keystores.
     */
    private static class GetKeyStoreOutputStreamAction implements PrivilegedExceptionAction<OutputStream> {
        private String file = null;

        /**
         * Constructor.
         * 
         * @param fileName
         */
        public GetKeyStoreOutputStreamAction(String fileName) {
            file = fileName;
        }

        @Override
        public OutputStream run() throws MalformedURLException, IOException {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.entry(tc, "GetKeyStoreOutputStreamAction.run: " + file);

            OutputStream fos = null;

            if (file.startsWith("safkeyring://")) {
                URL ring = new URL(file);
                URLConnection ringConnect = ring.openConnection();
                fos = ringConnect.getOutputStream();
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                    Tr.exit(tc, "GetKeyStoreOutputStreamAction.run (safkeyring)");
                return fos;
            }

            try {
                URL conversionURL = new URL(file);
                file = conversionURL.getFile();

                while (file.startsWith("/")) {
                    file = file.substring(1);
                }
            } catch (MalformedURLException e) {
                // it must be a file path already. just let it continue..
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "File path for OutputStream: " + file);

            // Check if the filename exists as a File.
            File kfile = new File(file);

            if (kfile.exists() && !kfile.canWrite()) {
                // kfile exists, but cannot write to it.
                throw new IOException("Cannot write to KeyStore file: " + file);
            }
            // kfile exists, updating it.
            fos = new FileOutputStream(kfile);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "GetKeyStoreOutputStreamAction.run");
            return fos;
        }
    }

    /**
     * This class is used to check if file exists.
     */
    private static class FileExistsAction implements PrivilegedAction<Boolean> {
        private File file = null;

        /**
         * Constructor.
         * 
         * @param input_file
         */
        public FileExistsAction(File input_file) {
            file = input_file;
        }

        @Override
        public Boolean run() {
            try {
                return Boolean.valueOf(file.exists());
            } catch (Exception e) {
                // it must be a file path already. just let it continue..
                return Boolean.FALSE;
            }
        }
    }

    /***
     * This method is used to create a "SHA-1" or "MD5" digest on an
     * X509Certificate as the "fingerprint".
     * 
     * @param algorithmName
     * @param cert
     * @return String
     ***/
    public String generateDigest(String algorithmName, X509Certificate cert) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "generateDigest: " + algorithmName);

        String rc = null;
        if (cert != null) {
            try {
                MessageDigest md = MessageDigest.getInstance(algorithmName);
                md.update(cert.getEncoded());
                byte data[] = md.digest();
                StringBuilder buffer = new StringBuilder(3 * data.length);
                int i = 0;
                buffer.append(HEX_CHARS[(data[i] >> 4) & 0xF]);
                buffer.append(HEX_CHARS[(data[i] % 16) & 0xF]);
                for (++i; i < data.length; i++) {
                    buffer.append(':');
                    buffer.append(HEX_CHARS[(data[i] >> 4) & 0xF]);
                    buffer.append(HEX_CHARS[(data[i] % 16) & 0xF]);
                }
                rc = buffer.toString();
            } catch (NoClassDefFoundError e) {
                // no ffdc needed, this is for PAC.
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Error finding a class: " + e);
            } catch (Exception e) {
                FFDCFilter.processException(e, getClass().getName(), "generateDigest", this);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "Error generating digest: " + e);
            }
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Ignoring null certificate");
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "generateDigest: " + rc);
        return rc;
    }

    /***
     * This method is used to clear KeyStore configurations when the entire config
     * is being reloaded.
     ***/
    public void clearKSMap() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "Clearing keystore maps");
        keyStoreMap.clear();
    }

    /***
     * This method is used to clear a specific KeyStore configuration when adding
     * a signer to it.
     ***/
    public void clearKeyStoreFromMap(String keyStoreName) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "clearKeyStoreFromMap: " + keyStoreName);
        keyStoreMap.remove(keyStoreName);
    }

    /***
     * This method is used to clear the Java KeyStores held within the WSKeyStores
     * in the KeyStoreMap. It's called after a federation.
     ***/
    public void clearJavaKeyStoresFromKeyStoreMap() {
        synchronized (keyStoreMap) {
            for (Entry<String, WSKeyStore> entry : keyStoreMap.entrySet()) {
                WSKeyStore ws = entry.getValue();

                if (ws != null)
                    ws.clearJavaKeyStore();
            }
        }
    }

    /***
     * Expands the ${hostname} with the node's hostname.
     * 
     * @param subjectDN
     * @param nodeHostName
     * @return String
     ***/
    public static String expandHostNameVariable(String subjectDN, String nodeHostName) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "expandHostNameVariable", new Object[] { subjectDN, nodeHostName });

        String expandedSubjectDN = subjectDN;
        int index1 = subjectDN.indexOf("${hostname}");

        if (index1 != -1) {
            String firstPart = subjectDN.substring(0, index1);
            String lastPart = subjectDN.substring(index1 + "${hostname}".length());
            // String.substring always returns non-null
            if (!firstPart.equals("") && !lastPart.equals(""))
                expandedSubjectDN = firstPart + nodeHostName + lastPart;
            else if (!firstPart.equals(""))
                expandedSubjectDN = firstPart + nodeHostName;
            else if (!lastPart.equals(""))
                expandedSubjectDN = nodeHostName + lastPart;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "expandHostNameVariable -> " + expandedSubjectDN);
        return expandedSubjectDN;
    }

    /**
     * Return the root Key Store for the process if it exists. Return null if the
     * root keyStore is not found.
     * 
     * @param defaultKeyStoreSuffix
     * @return WSKeyStore if found, null otherwise
     */
    public static WSKeyStore getDefaultKeyStore(String defaultKeyStoreSuffix) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getDefaultKeyStore");

        String keyStoreName = getDefaultKeyStoreName(defaultKeyStoreSuffix);
        WSKeyStore keyStore = getInstance().getKeyStore(keyStoreName);
        // TODO: scope SecurityConfigObject keyStore = null;
        //
        // try
        // {
        // SecurityConfigManager scm =
        // SecurityObjectLocator.getSecurityConfigManager();
        //
        // if (scm == null) {
        // if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        // Tr.debug(tc, "SecurityConfigManager is null.");
        // return null;
        // }
        //
        // SecurityConfigObjectList keyStores =
        // scm.getObjectList("security::keyStores");
        //
        // if (keyStores != null) {
        // for (int i = 0; i < keyStores.size(); i++) {
        // keyStore = keyStores.get(i);
        //
        // // check for matching name
        // String name = keyStore.getString("name");
        // if (keyStoreName != null && name.equals(keyStoreName)) {
        // if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        // Tr.exit(tc, "getDefaultKeyStore", new Object[]{name});
        // return keyStore;
        // }
        // // did not find it, set to null
        // keyStore = null;
        // }
        // } else {
        // // if keyStores is null, then we were unable to get the data from the
        // SCM.
        // // This could be a case where we are running in a client, clients do not
        // // have access to these defaultkeyStores, so return null
        // }
        // }
        // catch (Exception e)
        // {
        // if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
        // Tr.debug(tc, "Unable to get the DefaultKeyStore " + keyStoreName , new
        // Object[]{e});
        // }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getDefaultKeyStore", new Object[] { keyStore });
        return keyStore;
    }

    /**
     * Returns the name of the DefaultKeyStore for the node.
     * 
     * @param defaultKeyStoreSuffix
     * @return the name of the DefaultKeyStore
     */
    public static String getDefaultKeyStoreName(String defaultKeyStoreSuffix) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getDefaultKeyStoreName");

        // TODO scope String processType =
        // ManagementScopeManager.getInstance().getProcessType();
        // String keyStoreName = null;
        //
        // if(processType.equals("client"))
        // {
        // //clients do not have root keystores, return null
        // if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        // Tr.exit(tc, "getDefaultKeyStoreName: null");
        // return null;
        // }
        // else if (processType.equals("UnManagedProcess") ||
        // processType.equals("AdminAgent") || processType.equals("JobManager"))
        // {
        // //we are running in a standalone appserver
        // keyStoreName = Constants.UNMANAGED_KEY_STORE + defaultKeyStoreSuffix;
        // //do these need to be configurable
        // }
        // else
        // {
        // if (defaultKeyStoreSuffix.equalsIgnoreCase(Constants.DEFAULT_KEY_STORE)
        // ||
        // defaultKeyStoreSuffix.equalsIgnoreCase(Constants.DEFAULT_TRUST_STORE) ||
        // defaultKeyStoreSuffix.equalsIgnoreCase(Constants.RSA_TOKEN_KEY_STORE) ||
        // defaultKeyStoreSuffix.equalsIgnoreCase(Constants.RSA_TOKEN_TRUST_STORE))
        // {
        // keyStoreName = Constants.MANAGED_CELL_STORE + defaultKeyStoreSuffix;
        // }
        // else
        // {
        // keyStoreName = Constants.MANAGED_DMGR_STORE + defaultKeyStoreSuffix;
        // }
        // }
        //
        String keyStoreName = defaultKeyStoreSuffix;
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getDefaultKeyStoreName: " + keyStoreName);
        return keyStoreName;
    }

    /**
     * Remove the last slash, if present, from the input string and return the
     * result.
     * 
     * @param inputString
     * @return String
     */
    public static String stripLastSlash(String inputString) {
        if (null == inputString) {
            return null;
        }
        String rc = inputString.trim();
        int len = rc.length();
        if (0 < len) {
            char lastChar = rc.charAt(len - 1);
            if ('/' == lastChar || '\\' == lastChar) {
                rc = rc.substring(0, len - 1);
            }
        }

        return rc;
    }

    /**
     * Returns the java keystore object based on the keystore name passed in.
     * 
     * @param keyStoreName
     * @return KeyStore
     * @throws Exception
     */
    public KeyStore getJavaKeyStore(String keyStoreName) throws Exception {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getJavaKeyStore: " + keyStoreName);

        if (keyStoreName == null || keyStoreName.trim().isEmpty()) {
            throw new SSLException("No keystore name provided.");
        }

        KeyStore javaKeyStore = null;
        WSKeyStore ks = keyStoreMap.get(keyStoreName);
        if (ks != null) {
            javaKeyStore = ks.getKeyStore(false, false);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getJavaKeyStore: " + javaKeyStore);
        return javaKeyStore;
    }

    /**
     * Returns the java keystore object based on the keystore name passed in. A
     * null value is returned if no existing store matchs the provided name.
     * 
     * @param keyStoreName
     * @return WSKeyStore
     * @throws SSLException
     *             - if the input name is null
     */
    public WSKeyStore getWSKeyStore(String keyStoreName) throws SSLException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getWSKeyStore: " + keyStoreName);

        if (keyStoreName == null) {
            throw new SSLException("No keystore name provided.");
        }

        WSKeyStore ks = keyStoreMap.get(keyStoreName);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getWSKeyStore: " + ks);
        return ks;
    }

}
