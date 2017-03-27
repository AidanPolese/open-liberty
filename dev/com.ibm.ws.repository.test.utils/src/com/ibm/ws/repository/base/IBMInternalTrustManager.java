package com.ibm.ws.repository.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * An X509 trust manager which trusts the IBM Internal Root CA certificate.
 * <p>
 * This class wraps another X509TrustManager which it delegates everything to,
 * except that it additionally returns the IBM certificate from {@link #getAcceptedIssuers()}.
 */
public class IBMInternalTrustManager implements X509TrustManager {

    private final X509TrustManager realX509TM;
    private X509Certificate ibmInternalRootCA = null;
    private CertificateFactory certificateFactory;
    private CertPathValidator validator;

    /**
     * @param realX509TM the trust manager to delegate to
     * @throws IBMInternalTrustManagerException if a problem occurs while creating the trust manager
     */
    public IBMInternalTrustManager(X509TrustManager realX509TM) throws IBMInternalTrustManagerException {
        this.realX509TM = realX509TM;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream stream = new FileInputStream(new File("lib/LibertyFATTestFiles/IBMInternalRootCA.der"));
            ibmInternalRootCA = (X509Certificate) certificateFactory.generateCertificate(stream);
            validator = CertPathValidator.getInstance("PKIX");
        } catch (CertificateException e) {
            throw new IBMInternalTrustManagerException("Failed to create internal trust manager", e);
        } catch (FileNotFoundException e) {
            throw new IBMInternalTrustManagerException("Failed to load IBM Internal Root CA cert", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IBMInternalTrustManagerException("Failed to create internal trust manager", e);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // Make a copy of the array of certificates and add the
        // IBM Internal CA Cert to the end
        X509Certificate[] certs = realX509TM.getAcceptedIssuers();
        X509Certificate[] newCerts = Arrays.copyOf(certs, certs.length + 1);
        newCerts[newCerts.length - 1] = ibmInternalRootCA;
        return newCerts;
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
        // Set up a CertPath for validation with the IBM internal certificate as the only trust anchor
        Set<TrustAnchor> anchors = Collections.singleton(new TrustAnchor(ibmInternalRootCA, null));
        CertPath path = certificateFactory.generateCertPath(Arrays.asList(chain));
        try {
            PKIXParameters params = new PKIXParameters(anchors);
            params.setRevocationEnabled(false);

            validator.validate(path, params);
        } catch (CertPathValidatorException e) {
            // We couldn't validate the chain, let our parent have a go
            // This is expected for any chains which don't end with the IBM internal root CA
            realX509TM.checkServerTrusted(chain, authType);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateException(e);
        }

    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
        realX509TM.checkClientTrusted(chain, authType);
    }

    /**
     * Create an SSLSocketFactory which uses an IBMInternalTrustManager.
     * 
     * @return an SSLSocketFactory
     * @throws IBMInternalTrustManagerException if the operation fails
     */
    public static SSLSocketFactory getSSLSocketFactry() throws IBMInternalTrustManagerException {

        try {
            // Initialize the trust manager factory
            TrustManagerFactory tmFactory = TrustManagerFactory.getInstance("PKIX");
            tmFactory.init((KeyStore) null);

            // Find a real X509TrustManager that we can extend
            TrustManager[] realTMs = tmFactory.getTrustManagers();
            X509TrustManager x509TM = null;
            for (TrustManager tm : realTMs) {
                if (tm instanceof X509TrustManager) {
                    x509TM = (X509TrustManager) tm;
                    break;
                }
            }

            // Create our new trust manager which trusts the IBM Root CA Cert
            if (x509TM == null) {
                throw new IBMInternalTrustManagerException("Couldn't find an existing Trust Manager to extend");
            }

            TrustManager internalTM = new IBMInternalTrustManager(x509TM);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { internalTM }, null);
            return sc.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new IBMInternalTrustManagerException("Failed to create internal SSLSocketFactory", e);
        } catch (KeyManagementException e) {
            throw new IBMInternalTrustManagerException("Failed to create internal SSLSocketFactory", e);
        } catch (KeyStoreException e) {
            throw new IBMInternalTrustManagerException("Failed to create internal SSLSocketFactory", e);
        }
    }
}
