package com.ibm.ws.crypto.certificateutil;

import com.ibm.ws.crypto.certificateutil.keytool.KeytoolSSLCertificateCreator;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 * Extension point for future enhancement, specifically creating the
 * certificate using IBM-JVM APIs, which do not exist on other JVMs.
 */
public class DefaultSSLCertificateFactory {
    private static DefaultSSLCertificateCreator creator = new KeytoolSSLCertificateCreator();

    /**
     * Returns an implementation of a DefaultSSLCertificateCreator.
     * 
     * @return
     */
    public static DefaultSSLCertificateCreator getDefaultSSLCertificateCreator() {
        return creator;
    }

    /**
     * Controls which implementation of DefaultSSLCertificateCreator to return.
     * 
     * @param obj
     */
    public static void setDefaultSSLCertificateCreator(DefaultSSLCertificateCreator creator) {
        DefaultSSLCertificateFactory.creator = creator;
    }
}
