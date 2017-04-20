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
package com.ibm.ws.security.registry;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 *
 */
public class CertificateMapNotSupportedExceptionTest {

    /**
     * Test method for {@link com.ibm.ws.security.registry.CertificateMapNotSupportedException#CertificateMapNotSupportedException(java.lang.String)}.
     */
    @Test
    public void consturctor() {
        assertNotNull("Constructor(String) should succeed",
                      new CertificateMapNotSupportedException("msg"));
    }

}
