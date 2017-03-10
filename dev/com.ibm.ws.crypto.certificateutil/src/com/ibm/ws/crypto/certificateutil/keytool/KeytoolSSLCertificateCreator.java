/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.crypto.certificateutil.keytool;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import com.ibm.ws.crypto.certificateutil.DefaultSSLCertificateCreator;

/**
 *
 */
public class KeytoolSSLCertificateCreator implements DefaultSSLCertificateCreator {

    /** {@inheritDoc} */
    @Override
    public File createDefaultSSLCertificate(String filePath, String password, int validity, String subjectDN, int keySize, String sigAlg)
                    throws CertificateException {

        validateParameters(filePath, password, validity, subjectDN, keySize, sigAlg);

        String keyType = getKeyFromSigAlg(sigAlg);

        KeytoolCommand keytoolCmd = new KeytoolCommand(filePath, password, validity, subjectDN, keySize, keyType, sigAlg);
        keytoolCmd.executeCommand();
        File f = new File(filePath);
        if (f.exists()) {
            return f;
        } else {
            throw new CertificateException("KeytoolCommand executed successfully but file does not exist.");
        }
    }

    /**
     * Validate the parameters.
     * 
     * @param filePath
     * @param password
     * @param validity
     * @param subjectDN
     */
    private void validateParameters(String filePath, String password, int validity, String subjectDN, int keySize, String sigAlg) {
        if (!validateFilePath(filePath)) {
            throw new IllegalArgumentException("filePath must be a valid filePath within the file system.");
        }
        if (password == null || password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("The password must be at least " +
                                               MINIMUM_PASSWORD_LENGTH +
                                               " characters in length.");
        }
        if (validity < MINIMUM_VALIDITY) {
            throw new IllegalArgumentException("The validity period must be at least " +
                                               MINIMUM_VALIDITY + " days.");
        }

        List<String> validSigAlg = VALID_SIG_ALG;
        if (!validSigAlg.contains(sigAlg)) {
            throw new IllegalArgumentException("The signagure algorithm values include " + VALID_SIG_ALG);
        }

        String type = getKeyFromSigAlg(sigAlg);
        if (type.equals(KEYALG_RSA_TYPE)) {
            List<Integer> validKeySizes = VALID_RSA_KEYSIZE;
            if (!validKeySizes.contains(keySize)) {
                throw new IllegalArgumentException("The key sizes for an RSA key include " + VALID_RSA_KEYSIZE);
            }
        } else
        {
            List<Integer> validKeySizes = VALID_EC_KEYSIZE;
            if (!validKeySizes.contains(keySize)) {
                throw new IllegalArgumentException("The key sizes for an EC key include " + VALID_EC_KEYSIZE);
            }
        }

        validateSubjectDN(subjectDN);
    }

    /**
     * The specified filePath must either exist, or in the case the file
     * should be created, its parent directory.
     * 
     * @param loc
     * @return
     */
    private boolean validateFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath must be a valid filePath within the file system.");
        }

        // Check if the filename exists as a File -- use an absolute file to ensure we have
        // a parent: even if that parent is ${user.dir} ... 
        File loc = new File(filePath).getAbsoluteFile();

        return (loc.exists() || loc.getParentFile().exists());
    }

    /**
     * @param subjectDN
     */
    private void validateSubjectDN(String subjectDN) {
        if (subjectDN == null || subjectDN.isEmpty()) {
            throw new IllegalArgumentException("The subject DN must be a valid DN");
        }

        // Validate the subjectDN
        try {
            new LdapName(subjectDN);
        } catch (InvalidNameException e) {
            throw new IllegalArgumentException("The subject DN must be a valid DN");
        }
    }

    /**
     * @param sigAlg
     * @return
     */
    private String getKeyFromSigAlg(String sigAlg) {

        if (sigAlg.endsWith("ECDSA"))
            return KEYALG_EC_TYPE;
        else
            return KEYALG_RSA_TYPE;
    }

}
