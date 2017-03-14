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
package com.ibm.ws.crypto.certificateutil;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ibm.ws.crypto.certificateutil.keytool.KeytoolSSLCertificateCreator;

/**
 *
 */
public class DefaultSSLCertificateFactoryTest {

    /**
     * Test method for {@link com.ibm.ws.crypto.certificateutil.DefaultSSLCertificateFactory#getDefaultSSLCertificateCreator()}.
     */
    @Test
    public void getDefaultSSLCertificateCreator() {
        assertTrue("Was not the expected KeytoolSSLCertificateCreator instance",
                   DefaultSSLCertificateFactory.getDefaultSSLCertificateCreator() instanceof KeytoolSSLCertificateCreator);
    }

    /**
     * Test method for {@link com.ibm.ws.crypto.certificateutil.DefaultSSLCertificateFactory#getDefaultSSLCertificateCreator()}.
     */
    @Test
    public void setDefaultSSLCertificateCreator() {
        KeytoolSSLCertificateCreator creator = new KeytoolSSLCertificateCreator();
        DefaultSSLCertificateFactory.setDefaultSSLCertificateCreator(creator);
        assertSame("Was not the expected KeytoolSSLCertificateCreator instance",
                   creator, DefaultSSLCertificateFactory.getDefaultSSLCertificateCreator());
    }

}
